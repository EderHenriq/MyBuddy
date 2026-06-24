package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.DTO.AvaliacaoProdutoResponseDTO;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.AvaliacaoProdutoService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvaliacaoProdutoController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class AvaliacaoProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvaliacaoProdutoService avaliacaoProdutoService;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    private AvaliacaoProdutoResponseDTO avaliacaoResponse;
    private Usuario usuario;

    private static final String AVALIACAO_JSON = """
            {
                "nota": 5,
                "comentario": "Produto excelente!"
            }
            """;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Cliente Teste");

        avaliacaoResponse = AvaliacaoProdutoResponseDTO.builder()
                .id(1L)
                .produtoId(10L)
                .clienteId(1L)
                .clienteNome("Cliente Teste")
                .nota(5)
                .comentario("Produto excelente!")
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    // ===================== POST /api/produtos/{produtoId}/avaliacoes — autenticado =====================

    @Test
    void criar_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(post("/api/produtos/10/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AVALIACAO_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criar_ComUsuarioAutenticado_DeveRetornar201() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(avaliacaoProdutoService.criar(eq(10L), any(), any(Usuario.class))).thenReturn(avaliacaoResponse);

        mockMvc.perform(post("/api/produtos/10/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AVALIACAO_JSON)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nota").value(5))
                .andExpect(jsonPath("$.comentario").value("Produto excelente!"));
    }

    // ===================== GET /api/produtos/{produtoId}/avaliacoes — público =====================

    @Test
    void listarPorProduto_SemToken_DeveRetornar200() throws Exception {
        when(avaliacaoProdutoService.listarPorProduto(10L)).thenReturn(List.of(avaliacaoResponse));

        mockMvc.perform(get("/api/produtos/10/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nota").value(5))
                .andExpect(jsonPath("$[0].clienteNome").value("Cliente Teste"));
    }

    @Test
    void listarPorProduto_ComListaVazia_DeveRetornar200() throws Exception {
        when(avaliacaoProdutoService.listarPorProduto(10L)).thenReturn(List.of());

        mockMvc.perform(get("/api/produtos/10/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
