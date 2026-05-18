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
            log.info("Status no MP para paymentId={}: {}", event.getPaymentId(), mpStatus);

            paymentService.findByMpPaymentId(event.getPaymentId())
                    .ifPresentOrElse(payment -> {
                        PaymentStatus newStatus = mapStatus(mpStatus);
                        paymentService.updateStatus(payment, newStatus);
                        log.info("Payment {} atualizado para {}", payment.getId(), newStatus);
                    }, () -> log.warn("Payment não encontrado para mpPaymentId={}", event.getPaymentId()));

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