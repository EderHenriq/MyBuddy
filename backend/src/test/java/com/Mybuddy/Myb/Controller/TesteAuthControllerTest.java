package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TesteAuthController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class TesteAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    // ===================== GET /api/teste/autenticado =====================

    @Test
    void autenticado_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/teste/autenticado"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void autenticado_ComQualquerRole_DeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/teste/autenticado")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Você está autenticado!"));
    }

    // ===================== GET /api/teste/ong =====================

    @Test
    void ong_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/teste/ong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ong_ComRoleErrada_DeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/teste/ong")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void ong_ComRoleOng_DeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/teste/ong")
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isOk())
                .andExpect(content().string("Você é uma ONG!"));
    }

    // ===================== GET /api/teste/admin =====================

    @Test
    void admin_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/teste/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void admin_ComRoleErrada_DeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/teste/admin")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_ComRoleAdmin_DeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/teste/admin")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Você é um Admin!"));
    }

    // ===================== GET /api/teste/adotante =====================

    @Test
    void adotante_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/teste/adotante"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adotante_ComRoleErrada_DeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/teste/adotante")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adotante_ComRoleAdotante_DeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/teste/adotante")
                        .with(jwt().authorities(() -> "ROLE_ADOTANTE")))
                .andExpect(status().isOk())
                .andExpect(content().string("Você é um Adotante!"));
    }
}
