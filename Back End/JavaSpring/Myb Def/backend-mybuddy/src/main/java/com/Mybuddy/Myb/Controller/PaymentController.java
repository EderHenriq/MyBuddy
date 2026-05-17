package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.PaymentCreationResult;
import com.Mybuddy.Myb.DTO.PaymentRequestDTO;
import com.Mybuddy.Myb.DTO.PaymentResponseDTO;
import com.Mybuddy.Myb.Event.PaymentWebhookEvent;
import com.Mybuddy.Myb.Model.Payment;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.PaymentService;
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
    public ResponseEntity<Void> webhook(@RequestBody Map<String,
         Object> payload, @RequestHeader Map<String, String> headers) {
        
        log.info("Webhook MP recebido: {}", payload);

        String topic = (String) payload.get("topic");
        String type = (String) payload.get("type");

        if ("payment".equals(topic) || "payment".equals(type)) {
            Object dataId = payload.get("id");
            if (dataId == null && payload.get("data") instanceof Map<?,?> data) {
                dataId = data.get("id");
            }
            if (dataId != null) {
                eventPublisher.publishEvent(
                    new PaymentWebhookEvent(this, dataId.toString(), topic != null ? topic : type)
                );
            } else {
                log.warn("Webhook MP recebido sem ID: {}", payload);
            }
        }
        return ResponseEntity.ok().build();
    }
}
