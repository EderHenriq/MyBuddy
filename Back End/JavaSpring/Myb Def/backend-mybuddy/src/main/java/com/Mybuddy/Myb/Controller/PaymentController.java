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
// import com.mercadopago.client.preapproval.PreapprovalRequest;
import com.mercadopago.resources.preapproval.Preapproval;
import com.Mybuddy.Myb.Repository.jpa.DonationSubscriptionRepository;
import com.Mybuddy.Myb.Model.DonationSubscription;

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
import java.time.LocalDateTime;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    private final KeycloakUserSyncService keycloakUserSyncService;
    private final ApplicationEventPublisher eventPublisher;
    private final MercadoPagoWebhookValidator webhookValidator;
    private final DonationSubscriptionRepository donationSubscriptionRepository;

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

        log.info("Webhook MP recebido: {}", payload);
        
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
}