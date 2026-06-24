package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.DTO.CategoriaResponseDTO;
import com.Mybuddy.Myb.DTO.SubCategoriaResponseDTO;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.CategoriaService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    private CategoriaResponseDTO categoriaResponse;
    private SubCategoriaResponseDTO subCategoriaResponse;

    private static final String CATEGORIA_JSON = """
            {"nome": "Ração"}
            """;

    private static final String SUBCATEGORIA_JSON = """
            {"nome": "Ração Seca", "categoriaId": 1}
            """;

    @BeforeEach
    void setUp() {
        subCategoriaResponse = new SubCategoriaResponseDTO(10L, "Ração Seca", 1L);
        categoriaResponse = new CategoriaResponseDTO(1L, "Ração", List.of(subCategoriaResponse));
    }

    // ===================== GET /api/categorias — público =====================

    @Test
    void listarTodas_SemToken_DeveRetornar200() throws Exception {
        when(categoriaService.listarTodas()).thenReturn(List.of(categoriaResponse));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ração"));
    }

    @Test
    void listarTodas_ComListaVazia_DeveRetornar200() throws Exception {
        when(categoriaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ===================== GET /api/categorias/{id} — público =====================

    @Test
    void buscarPorId_SemToken_DeveRetornar200() throws Exception {
        when(categoriaService.buscarPorId(1L)).thenReturn(categoriaResponse);

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Ração"));
    }

    // ===================== POST /api/categorias — ADMIN =====================

    @Test
    void criar_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORIA_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criar_ComRoleUser_DeveRetornar403() throws Exception {
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORIA_JSON)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void criar_ComRoleAdmin_DeveRetornar201() throws Exception {
        when(categoriaService.criar(any())).thenReturn(categoriaResponse);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORIA_JSON)
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Ração"));
    }

    // ===================== POST /api/categorias/subcategorias — ADMIN =====================

    @Test
    void criarSubcategoria_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(post("/api/categorias/subcategorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SUBCATEGORIA_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criarSubcategoria_ComRoleAdmin_DeveRetornar201() throws Exception {
        when(categoriaService.criarSubcategoria(any())).thenReturn(subCategoriaResponse);

        mockMvc.perform(post("/api/categorias/subcategorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SUBCATEGORIA_JSON)
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Ração Seca"));
    }

    // ===================== DELETE /api/categorias/{id} — ADMIN =====================

    @Test
    void deletar_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deletar_ComRoleUser_DeveRetornar403() throws Exception {
        mockMvc.perform(delete("/api/categorias/1")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletar_ComRoleAdmin_DeveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/categorias/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNoContent());

        verify(categoriaService).deletar(eq(1L));
    }

    // ===================== DELETE /api/categorias/subcategorias/{id} — ADMIN =====================

    @Test
    void deletarSubcategoria_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(delete("/api/categorias/subcategorias/10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deletarSubcategoria_ComRoleAdmin_DeveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/categorias/subcategorias/10")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNoContent());

        verify(categoriaService).deletarSubcategoria(eq(10L));
    }
}
