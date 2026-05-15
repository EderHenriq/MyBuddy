package com.Mybuddy.Myb.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthConverterTest {

    private JwtAuthConverter jwtAuthConverter;

    @BeforeEach
    void setUp() {
        jwtAuthConverter = new JwtAuthConverter();
    }

    private Jwt buildJwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claims(c -> c.putAll(claims))
                .build();
    }

    @Test
    void deveExtrairRoleOngDoRealmAccess() {
        Jwt jwt = buildJwt(Map.of(
                "sub", "user-123",
                "preferred_username", "davi",
                "realm_access", Map.of("roles", List.of("ONG"))
        ));

        AbstractAuthenticationToken token = jwtAuthConverter.convert(jwt);
        Collection<GrantedAuthority> authorities = token.getAuthorities();

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ONG");
    }

    @Test
    void deveExtrairRoleAdminDoRealmAccess() {
        Jwt jwt = buildJwt(Map.of(
                "sub", "user-123",
                "preferred_username", "davi",
                "realm_access", Map.of("roles", List.of("ADMIN"))
        ));

        AbstractAuthenticationToken token = jwtAuthConverter.convert(jwt);

        assertThat(token.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN");
    }

    @Test
    void deveRetornarListaVaziaQuandoSemRealmAccess() {
        Jwt jwt = buildJwt(Map.of(
                "sub", "user-123",
                "preferred_username", "davi"
        ));

        AbstractAuthenticationToken token = jwtAuthConverter.convert(jwt);

        assertThat(token.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .doesNotContain("ROLE_ONG", "ROLE_ADMIN");
    }

    @Test
    void devePegarPreferredUsernameComoPrincipal() {
        Jwt jwt = buildJwt(Map.of(
                "sub", "user-123",
                "preferred_username", "davi",
                "realm_access", Map.of("roles", List.of("USER"))
        ));

        AbstractAuthenticationToken token = jwtAuthConverter.convert(jwt);

        assertThat(token.getName()).isEqualTo("davi");
    }
}