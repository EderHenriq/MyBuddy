package com.Mybuddy.Myb.Config;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

/**
 * Configura o SDK do Mercado Pago com o access token da conta da plataforma,
 * necessário para criar preferências de pagamento e assinaturas de doação.
 */
@Configuration
public class MercadoPagoConfig {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
    }
}
