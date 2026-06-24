package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.DTO.AgendamentoResponseDTO;
import com.Mybuddy.Myb.Model.StatusAgendamento;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.AgendamentoService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendamentoController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgendamentoService agendamentoService;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    private AgendamentoResponseDTO agendamentoResponse;
    private Usuario usuario;

    private static final String AGENDAMENTO_JSON = """
            {
                "petId": 1,
                "servicoId": 1,
                "dataHoraInicio": "2030-06-01T10:00:00",
                "profissionalNome": "Ana"
            }
            """;

    private static final String STATUS_JSON = """
            {"status": "CANCELADO"}
            """;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        agendamentoResponse = AgendamentoResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .petId(1L)
                .servicoId(1L)
                .servicoNome("Banho")
                .preco(new BigDecimal("60.00"))
                .status(StatusAgendamento.AGENDADO)
                .build();
    }

    // ===================== POST /api/agendamentos =====================

    @Test
    void criar_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(post("/api/agendamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AGENDAMENTO_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criar_ComUsuarioAutenticado_DeveRetornar201() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(agendamentoService.criar(any(), any(Usuario.class))).thenReturn(agendamentoResponse);

        mockMvc.perform(post("/api/agendamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AGENDAMENTO_JSON)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(header().string("Location", "/api/agendamentos/1"));
    }

    // ===================== GET /api/agendamentos/cliente =====================

    @Test
    void listarPorCliente_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/agendamentos/cliente"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarPorCliente_ComUsuarioAutenticado_DeveRetornar200() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(agendamentoService.listarPorCliente(any(Usuario.class))).thenReturn(List.of(agendamentoResponse));

        mockMvc.perform(get("/api/agendamentos/cliente")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ===================== GET /api/agendamentos/petshop =====================

    @Test
    void listarPorPetshop_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/agendamentos/petshop"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarPorPetshop_ComRoleUser_DeveRetornar403() throws Exception {
        mockMvc.perform(get("/api/agendamentos/petshop")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarPorPetshop_ComRolePetshop_DeveRetornar200() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(agendamentoService.listarPorPetshop(any(Usuario.class))).thenReturn(List.of(agendamentoResponse));

        mockMvc.perform(get("/api/agendamentos/petshop")
                        .with(jwt().authorities(() -> "ROLE_PETSHOP")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].servicoNome").value("Banho"));
    }

    @Test
    void listarPorPetshop_ComRoleAdmin_DeveRetornar200() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(agendamentoService.listarPorPetshop(any(Usuario.class))).thenReturn(List.of(agendamentoResponse));

        mockMvc.perform(get("/api/agendamentos/petshop")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk());
    }

    // ===================== PUT /api/agendamentos/{id}/status =====================

    @Test
    void atualizarStatus_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(put("/api/agendamentos/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(STATUS_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void atualizarStatus_ComUsuarioAutenticado_DeveRetornar200() throws Exception {
        AgendamentoResponseDTO cancelado = AgendamentoResponseDTO.builder()
                .id(1L)
                .status(StatusAgendamento.CANCELADO)
                .build();

        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(agendamentoService.atualizarStatus(eq(1L), eq(StatusAgendamento.CANCELADO), any(Usuario.class)))
                .thenReturn(cancelado);

        mockMvc.perform(put("/api/agendamentos/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(STATUS_JSON)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));
    }
}
