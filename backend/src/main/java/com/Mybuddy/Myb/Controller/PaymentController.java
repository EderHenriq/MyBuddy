package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.PaymentCreationResult;
import com.Mybuddy.Myb.DTO.PaymentRequestDTO;
import com.Mybuddy.Myb.DTO.PaymentResponseDTO;
import com.Mybuddy.Myb.Event.PaymentWebhookEvent;
import com.Mybuddy.Myb.Model.Payment;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.PaymentService;
import com.Mybuddy.Myb.Util.MercadoPagoWebhookValidator;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.client.preapproval.PreapprovalClient;
import com.mercadopago.client.preapproval.PreapprovalUpdateRequest;
import com.mercadopago.resources.preapproval.Preapproval;
import com.Mybuddy.Myb.Repository.jpa.DonationSubscriptionRepository;
import com.Mybuddy.Myb.Model.DonationSubscription;
import com.Mybuddy.Myb.Model.PaymentStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class PaymentController {
    
    private final PaymentService paymentService;
    private final KeycloakUserSyncService keycloakUserSyncService;
    private final ApplicationEventPublisher eventPublisher;
    private final MercadoPagoWebhookValidator webhookValidator;
    private final DonationSubscriptionRepository donationSubscriptionRepository;

    /**
     * Cria uma preferência de pagamento no Mercado Pago para o usuário autenticado
     * (compra, doação ou adoção com contribuição).
     *
     * @param jwt token do usuário autenticado
     * @param request dados do pagamento a ser criado
     * @return pagamento criado, incluindo o link de checkout
     */
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PaymentRequestDTO request) throws MPException, MPApiException {

        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        PaymentCreationResult result = paymentService.createPayment(usuario, request);
        Payment saved = result.payment();

        return ResponseEntity.ok(new PaymentResponseDTO(
                saved.getId(),
                saved.getMpPreferenceId(),
                saved.getMpPaymentId(),
                saved.getUsuarioId(),
                saved.getPetId(),
                saved.getCampanhaId(),
                saved.getOrganizacaoId(),
                saved.getAmount(),
                saved.getStatus(),
                result.initPoint(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        ));
    }

    @PostMapping("/subscribe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> createSubscription(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> payload) throws MPException, MPApiException {
        
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        String frequency = (String) payload.get("frequency"); // monthly ou weekly
        Long orgId = payload.get("organizacaoId") != null ? Long.valueOf(payload.get("organizacaoId").toString()) : null;

        PreapprovalClient client = new PreapprovalClient();
        
        // Define as datas da recorrência
        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
        java.time.OffsetDateTime end = now.plusYears(1); // Assinatura com validade de 1 ano

        com.mercadopago.client.preapproval.PreapprovalCreateRequest preapprovalRequest = 
                com.mercadopago.client.preapproval.PreapprovalCreateRequest.builder()
                .backUrl("http://localhost/checkout/confirmacao")
                .reason(frequency.equalsIgnoreCase("monthly") ? "Assinatura Mensal - MyBuddy" : "Assinatura Semanal - MyBuddy")
                .payerEmail(usuario.getEmail())
                .autoRecurring(
                        com.mercadopago.client.preapproval.PreApprovalAutoRecurringCreateRequest.builder()
                                .frequency(1)
                                .frequencyType(frequency.equalsIgnoreCase("monthly") ? "months" : "weeks")
                                .transactionAmount(amount)
                                .currencyId("BRL")
                                .startDate(now)
                                .endDate(end)
                                .build()
                )
                .build();

        Preapproval preapproval = client.create(preapprovalRequest);

        // Salvar a assinatura pendente localmente
        DonationSubscription sub = DonationSubscription.builder()
                .mpPreapprovalId(preapproval.getId())
                .usuarioId(usuario.getId())
                .organizacaoId(orgId)
                .amount(amount)
                .frequency(frequency)
                .status("pending")
                .build();
        donationSubscriptionRepository.save(sub);

        return ResponseEntity.ok(Map.of(
                "preapprovalId", preapproval.getId(),
                "initPoint", preapproval.getSandboxInitPoint() != null ? preapproval.getSandboxInitPoint() : preapproval.getInitPoint()
        ));
    }

    @PostMapping("/subscribe/{id}/cancelar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelSubscription(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) throws MPException, MPApiException {
        
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        
        DonationSubscription subscription = donationSubscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada: " + id));

        // Validar se a assinatura pertence ao usuário logado
        if (!subscription.getUsuarioId().equals(usuario.getId())) {
            throw new org.springframework.security.authorization.AuthorizationDeniedException("Acesso negado para cancelar esta assinatura.");
        }

        // Cancelar no Mercado Pago
        PreapprovalClient client = new PreapprovalClient();
        PreapprovalUpdateRequest request = PreapprovalUpdateRequest.builder()
                .status("cancelled")
                .build();
        
        client.update(subscription.getMpPreapprovalId(), request);

        // Atualizar banco local
        subscription.setStatus("cancelled");
        donationSubscriptionRepository.save(subscription);

        log.info("Assinatura cancelada com sucesso: id={}, mpPreapprovalId={}", subscription.getId(), subscription.getMpPreapprovalId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponseDTO> getPayment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        Payment payment = paymentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment não encontrado: " + id));

        return ResponseEntity.ok(new PaymentResponseDTO(
                payment.getId(),
                payment.getMpPreferenceId(),
                payment.getMpPaymentId(),
                payment.getUsuarioId(),
                payment.getPetId(),
                payment.getCampanhaId(),
                payment.getOrganizacaoId(),
                payment.getAmount(),
                payment.getStatus(),
                null,
                payment.getCreatedAt(),
                payment.getUpdatedAt()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook (
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "x-signature", required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId){

        log.info("Webhook MP recebido: topic={}, type={}", payload.get("topic"), payload.get("type"));
        
        String dataId = null;
        if (payload.get("data") instanceof Map<?, ?> data) {

            dataId = String.valueOf(data.get("id"));
        } else if (payload.get("id") != null) {
            dataId = String.valueOf(payload.get("id"));
        }

        if (!webhookValidator.isValid(xSignature, xRequestId, dataId)) {
            log.warn("Webhook MP recebido com assinatura inválida — ignorando. requestId={}", xRequestId);
            return ResponseEntity.ok().build();
        }

        String topic = (String) payload.get("topic");
        String type = (String) payload.get("type");

        if ("payment".equals(topic) || "payment".equals(type)) {
            eventPublisher.publishEvent(
                new PaymentWebhookEvent(this, dataId, topic != null ? topic : type));
        } else {
            log.info("Webhook MP ignorado — topic ou type não tratado. topic={}, type={}", topic, type);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/preference/{mpPreferenceId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByPreferenceId(
            @PathVariable String mpPreferenceId) {
        Payment payment = paymentService.findByMpPreferenceId(mpPreferenceId)
                .orElseThrow(() -> new RuntimeException("Payment não encontrado: " + mpPreferenceId));

        return ResponseEntity.ok(new PaymentResponseDTO(
                payment.getId(),
                payment.getMpPreferenceId(),
                payment.getMpPaymentId(),
                payment.getUsuarioId(),
                payment.getPetId(),
                payment.getCampanhaId(),
                payment.getOrganizacaoId(),
                payment.getAmount(),
                payment.getStatus(),
                null,
                payment.getCreatedAt(),
                payment.getUpdatedAt()));
    }

    @PostMapping("/sync-redirection")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponseDTO> syncRedirection(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String paymentId,
            @RequestParam(required = false) String preferenceId) throws MPException, MPApiException {
        
        log.info("Requisição de sincronização recebida: paymentId={}, preferenceId={}", paymentId, preferenceId);
        
        if (paymentId == null && preferenceId == null) {
            throw new IllegalArgumentException("paymentId ou preferenceId é obrigatório para sincronização.");
        }

        java.util.Optional<Payment> paymentOpt = java.util.Optional.empty();
        if (paymentId != null) {
            paymentOpt = paymentService.findByMpPaymentId(paymentId);
        }
        if (paymentOpt.isEmpty() && preferenceId != null) {
            paymentOpt = paymentService.findByMpPreferenceId(preferenceId);
        }

        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Pagamento local correspondente não encontrado.");
        }

        Payment payment = paymentOpt.get();

        if (payment.getStatus() == PaymentStatus.PENDING) {
            if (paymentId != null) {
                try {
                    com.mercadopago.client.payment.PaymentClient client = new com.mercadopago.client.payment.PaymentClient();
                    com.mercadopago.resources.payment.Payment mpPayment = client.get(Long.parseLong(paymentId));
                    String mpStatus = mpPayment.getStatus();
                    
                    if (payment.getMpPaymentId() == null) {
                        payment.setMpPaymentId(paymentId);
                    }

                    PaymentStatus newStatus = switch (mpStatus) {
                        case "approved" -> PaymentStatus.APPROVED;
                        case "rejected" -> PaymentStatus.REJECTED;
                        case "cancelled" -> PaymentStatus.CANCELLED;
                        case "refunded" -> PaymentStatus.REFUNDED;
                        default -> PaymentStatus.PENDING;
                    };
                    
                    if (newStatus != payment.getStatus()) {
                        paymentService.updateStatus(payment, newStatus);
                    }
                } catch (Exception e) {
                    log.error("Erro ao sincronizar com Mercado Pago para paymentId {}: {}", paymentId, e.getMessage());
                }
            }
        }

        return ResponseEntity.ok(new PaymentResponseDTO(
                payment.getId(),
                payment.getMpPreferenceId(),
                payment.getMpPaymentId(),
                payment.getUsuarioId(),
                payment.getPetId(),
                payment.getCampanhaId(),
                payment.getOrganizacaoId(),
                payment.getAmount(),
                payment.getStatus(),
                null,
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        ));
    }
}