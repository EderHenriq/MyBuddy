package com.Mybuddy.Myb.Config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * Configuração de segurança para testes.
 * Substitui o JwtDecoder padrão (que tentaria conectar ao Keycloak)
 * por um mock, permitindo que os testes @SpringBootTest subam sem
 * precisar de um servidor Keycloak rodando (ex: no CI/GitHub Actions).
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return Mockito.mock(JwtDecoder.class);
    }
}
