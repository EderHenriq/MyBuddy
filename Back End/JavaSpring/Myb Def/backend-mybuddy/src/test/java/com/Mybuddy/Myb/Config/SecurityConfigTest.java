package com.Mybuddy.Myb.Config;

import com.Mybuddy.Myb.Security.JwtAuthConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthConverter jwtAuthConverter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void deveRetornarBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }
}
