package com.Mybuddy.Myb.Config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Mybuddy.Myb.Security.jwt.AuthEntryPointJwt;
import com.Mybuddy.Myb.Security.jwt.AuthTokenFilter;
import com.Mybuddy.Myb.Security.jwt.UserDetailsServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthEntryPointJwt authEntryPointJwt;

    @Mock
    private AuthTokenFilter authTokenFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void deveRetornarBCryptPasswordEncoder() {
        // Act
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // Assert
        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void deveRetornarDaoAuthenticationProvider() {
        // Act
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();

        // Assert
        assertThat(provider).isNotNull();
    }
}