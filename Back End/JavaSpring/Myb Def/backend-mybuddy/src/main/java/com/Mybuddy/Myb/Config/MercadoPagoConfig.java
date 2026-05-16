package com.Mybuddy.Myb.Config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MercadoPagoConfig {
    
    @Value("${mercadopago.access_token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        System.setProperty("mercadopago.access_token", accessToken);
    }
}
