package com.Mybuddy.Myb.Util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

class MercadoPagoWebhookValidatorTest {

    private MercadoPagoWebhookValidator validator;

    private static final String SECRET = "test-secret-key";
    private static final String REQUEST_ID = "req-123";
    private static final String DATA_ID = "data-456";
    private static final String TS = "1700000000";

    @BeforeEach
    void setUp() {
        validator = new MercadoPagoWebhookValidator();
        ReflectionTestUtils.setField(validator, "webhookSecret", SECRET);
    }

    private String gerarAssinaturaValida(String ts, String requestId, String dataId) throws Exception {
        String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";";
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hashBytes = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
        return "ts=" + ts + ",v1=" + HexFormat.of().formatHex(hashBytes);
    }

    // ===================== ASSINATURA VÁLIDA =====================

    @Test
    void deveRetornarTrueParaAssinaturaValida() throws Exception {
        String xSignature = gerarAssinaturaValida(TS, REQUEST_ID, DATA_ID);

        boolean result = validator.isValid(xSignature, REQUEST_ID, DATA_ID);

        assertThat(result).isTrue();
    }

    // ===================== ASSINATURA AUSENTE =====================

    @Test
    void deveRetornarFalseQuandoXSignatureNulo() {
        boolean result = validator.isValid(null, REQUEST_ID, DATA_ID);

        assertThat(result).isFalse();
    }

    @Test
    void deveRetornarFalseQuandoXSignatureVazio() {
        boolean result = validator.isValid("", REQUEST_ID, DATA_ID);

        assertThat(result).isFalse();
    }

    @Test
    void deveRetornarFalseQuandoXSignatureEmBranco() {
        boolean result = validator.isValid("   ", REQUEST_ID, DATA_ID);

        assertThat(result).isFalse();
    }

    // ===================== ASSINATURA MALFORMADA =====================

    @Test
    void deveRetornarFalseQuandoAssinaturaSemTs() {
        boolean result = validator.isValid("v1=abc123", REQUEST_ID, DATA_ID);

        assertThat(result).isFalse();
    }

    @Test
    void deveRetornarFalseQuandoAssinaturaSemV1() {
        boolean result = validator.isValid("ts=" + TS, REQUEST_ID, DATA_ID);

        assertThat(result).isFalse();
    }

    @Test
    void deveRetornarFalseQuandoAssinaturaComFormatoInvalido() {
        boolean result = validator.isValid("invalido", REQUEST_ID, DATA_ID);

        assertThat(result).isFalse();
    }

    // ===================== ASSINATURA INCORRETA =====================

    @Test
    void deveRetornarFalseQuandoV1Incorreto() {
        String xSignature = "ts=" + TS + ",v1=assinatura-errada";

        boolean result = validator.isValid(xSignature, REQUEST_ID, DATA_ID);

        assertThat(result).isFalse();
    }

    @Test
    void deveRetornarFalseQuandoDataIdDiferente() throws Exception {
        String xSignature = gerarAssinaturaValida(TS, REQUEST_ID, DATA_ID);

        boolean result = validator.isValid(xSignature, REQUEST_ID, "outro-data-id");

        assertThat(result).isFalse();
    }

    @Test
    void deveRetornarFalseQuandoRequestIdDiferente() throws Exception {
        String xSignature = gerarAssinaturaValida(TS, REQUEST_ID, DATA_ID);

        boolean result = validator.isValid(xSignature, "outro-request-id", DATA_ID);

        assertThat(result).isFalse();
    }

    @Test
    void deveRetornarFalseQuandoTsDiferente() throws Exception {
        String xSignature = gerarAssinaturaValida(TS, REQUEST_ID, DATA_ID);
        String xSignatureOutroTs = xSignature.replace("ts=" + TS, "ts=9999999999");

        boolean result = validator.isValid(xSignatureOutroTs, REQUEST_ID, DATA_ID);

        assertThat(result).isFalse();
    }
}