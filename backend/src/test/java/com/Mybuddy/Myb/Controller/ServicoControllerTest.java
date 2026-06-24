package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.DTO.ServicoResponseDTO;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.ServicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServicoController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServicoService servicoService;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    private ServicoResponseDTO servicoResponse;
    private Usuario usuario;

    private static final String SERVICO_JSON = """
            {
                "nome": "Banho e Tosa",
                "descricao": "Banho completo com tosa",
                "preco": 80.00,
                "duracaoMinutos": 60,
                "ativo": true
            }
            """;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        servicoResponse = ServicoResponseDTO.builder()
                .id(1L)
                .nome("Banho e Tosa")
                .preco(new BigDecimal("80.00"))
                .duracaoMinutos(60)
                .petshopId(1L)
                .ativo(true)
                .build();
    }

    // ===================== POST /api/servicos — PETSHOP ou ADMIN =====================

    @Test
    void criar_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SERVICO_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criar_ComRoleUser_DeveRetornar403() throws Exception {
        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SERVICO_JSON)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void criar_ComRolePetshop_DeveRetornar201() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(servicoService.criar(any(), any(Usuario.class))).thenReturn(servicoResponse);

        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SERVICO_JSON)
                        .with(jwt().authorities(() -> "ROLE_PETSHOP")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Banho e Tosa"))
                .andExpect(header().string("Location", "/api/servicos/1"));
    }

    @Test
    void criar_ComRoleAdmin_DeveRetornar201() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(servicoService.criar(any(), any(Usuario.class))).thenReturn(servicoResponse);

        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SERVICO_JSON)
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isCreated());
    }

    // ===================== GET /api/servicos/petshop/{id} — autenticado =====================

    @Test
    void listarPorPetshop_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/servicos/petshop/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarPorPetshop_ComUsuarioAutenticado_DeveRetornar200() throws Exception {
        when(servicoService.listarPublicosPorPetshop(1L)).thenReturn(List.of(servicoResponse));

        mockMvc.perform(get("/api/servicos/petshop/1")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Banho e Tosa"))
                .andExpect(jsonPath("$[0].preco").value(80.00));
    }
}
