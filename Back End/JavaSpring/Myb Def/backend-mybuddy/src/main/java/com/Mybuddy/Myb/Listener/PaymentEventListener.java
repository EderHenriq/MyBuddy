package com.Mybuddy.Myb.Listener;

import com.Mybuddy.Myb.Event.PaymentWebhookEvent;
import com.Mybuddy.Myb.Model.PaymentStatus;
import com.Mybuddy.Myb.Service.PaymentService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;

    @Async
    @EventListener
    public void handlePaymentWebhook(PaymentWebhookEvent event) {
        log.info("Processando webhook: paymentId={}, topic={}", event.getPaymentId(), event.getTopic());

        try {
            PaymentClient client = new PaymentClient();
            Payment mpPayment = client.get(Long.parseLong(event.getPaymentId()));

            String mpStatus = mpPayment.getStatus();
            String tempPreferenceId = null;
            if (mpPayment.getResponse() != null && mpPayment.getResponse().getContent() != null) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(mpPayment.getResponse().getContent());
                    if (node.has("preference_id")) {
                        tempPreferenceId = node.get("preference_id").asText();
                    }
                } catch (Exception parseEx) {
                    log.error("Erro ao fazer parse do preference_id do JSON do MP: {}", parseEx.getMessage());
                }
            }
            final String preferenceId = tempPreferenceId;
            log.info("Status no MP para paymentId={} (preferenceId={}): {}", event.getPaymentId(), preferenceId, mpStatus);

            // Tenta localizar por mpPaymentId primeiro
            var localPaymentOpt = paymentService.findByMpPaymentId(event.getPaymentId());
            
            // Se não encontrar, localiza por mpPreferenceId
            if (localPaymentOpt.isEmpty() && preferenceId != null) {
                localPaymentOpt = paymentService.findByMpPreferenceId(preferenceId);
            }

            localPaymentOpt.ifPresentOrElse(payment -> {
                // Atualiza o id do pagamento do Mercado Pago caso esteja vazio
                if (payment.getMpPaymentId() == null) {
                    payment.setMpPaymentId(event.getPaymentId());
                }
                
                PaymentStatus newStatus = mapStatus(mpStatus);
                paymentService.updateStatus(payment, newStatus);
                log.info("Payment local {} (MP ID: {}) atualizado para {}", payment.getId(), event.getPaymentId(), newStatus);
            }, () -> log.warn("Payment não encontrado localmente para mpPaymentId={} ou preferenceId={}", event.getPaymentId(), preferenceId));

        } catch (MPException | MPApiException e) {
            log.error("Erro ao consultar MP para paymentId={}: {}", event.getPaymentId(), e.getMessage());
        } catch (NumberFormatException e) {
            log.error("paymentId inválido: {}", event.getPaymentId());
        }
    }

    private PaymentStatus mapStatus(String mpStatus) {
        return switch (mpStatus) {
            case "approved" -> PaymentStatus.APPROVED;
            case "rejected" -> PaymentStatus.REJECTED;
            case "cancelled" -> PaymentStatus.CANCELLED;
            case "refunded" -> PaymentStatus.REFUNDED;
            default -> PaymentStatus.PENDING;
        };
    }
}