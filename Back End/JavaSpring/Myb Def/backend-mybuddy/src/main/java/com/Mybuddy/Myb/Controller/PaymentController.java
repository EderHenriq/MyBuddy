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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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
                saved.getUsuario().getId(),
                saved.getPet() != null ? saved.getPet().getId() : null,
                saved.getAmount(),
                saved.getStatus(),
                result.initPoint(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
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
                payment.getUsuario().getId(),
                payment.getPet().getId(),
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
}
