package com.Mybuddy.Myb.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Slf4j
@Component
@SuppressWarnings("null")
public class MercadoPagoWebhookValidator {

    @Value("${mercadopago.webhook-secret}")
    private String webhookSecret;

    /**
     * Valida a assinatura HMAC-SHA256 de uma notificação de webhook do Mercado Pago,
     * conferindo o hash calculado com o valor `v1` enviado no cabeçalho `x-signature`.
     *
     * @param xSignature cabeçalho x-signature recebido do Mercado Pago
     * @param xRequestId cabeçalho x-request-id recebido do Mercado Pago
     * @param dataId identificador do recurso notificado (payment/subscription id)
     * @return {@code true} se a assinatura for válida, {@code false} caso contrário
     */
    public boolean isValid(String xSignature, String xRequestId, String dataId) {
        try{
            if(xSignature == null || xSignature.isBlank()) {
                log.warn("x-signature ausente no Webhook MP. requestId={}", xRequestId);
                return false;
            }

            String ts = null;
            String v1 = null;
            
            for (String part : xSignature.split(",")) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length == 2) {
                    if ("ts".equals(kv[0])) ts = kv[1];
                    if ("v1".equals(kv[0])) v1 = kv[1];
                }
            }

            if (ts == null || v1 == null) {
                log.warn("x-signature malformada no Webhook MP. requestId={}", xRequestId);
                return false;
            }

            String manifest = "id:" + dataId + ";request-id:" + xRequestId + ";ts:" + ts + ";";

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hashBytes = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));

            String computed = HexFormat.of().formatHex(hashBytes);
            boolean valid = computed.equals(v1);

            if (!valid) {
                log.warn("Validação falhou para webhook do Mercado Pago. requestId={}", xRequestId);
            }
            return valid;

        } catch (Exception e) {
            log.error("Erro ao validar webhook do Mercado Pago. requestId={}", xRequestId, e);
            return false;
        
        }
    }
}