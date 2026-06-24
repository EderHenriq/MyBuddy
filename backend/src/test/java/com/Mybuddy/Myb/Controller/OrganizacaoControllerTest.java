package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.DTO.OrganizacaoRequestDTO;
import com.Mybuddy.Myb.DTO.OrganizacaoResponseDTO;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.OrganizacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizacaoController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class OrganizacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrganizacaoService organizacaoService;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    private OrganizacaoResponseDTO orgResponse;
    private Usuario adminUser;
    private Usuario ongUser;

    private static final String ORG_JSON = """
            {
                "nomeFantasia": "ONG Patinhas",
                "emailContato": "contato@patinhas.org",
                "cnpj": "11.222.333/0001-81",
                "telefoneContato": "(11) 99999-9999",
                "endereco": "Rua das Flores, 123"
            }
            """;

    @BeforeEach
    void setUp() {
        Organizacao org = new Organizacao();
        org.setId(1L);

        orgResponse = new OrganizacaoResponseDTO();
        orgResponse.setId(1L);
        orgResponse.setNomeFantasia("ONG Patinhas");
        orgResponse.setEmailContato("contato@patinhas.org");

        adminUser = new Usuario();
        adminUser.setId(1L);
        adminUser.setRoles(Set.of(new Role(ERole.ROLE_ADMIN)));

        ongUser = new Usuario();
        ongUser.setId(2L);
        ongUser.setOrganizacao(org);
        ongUser.setRoles(Set.of(new Role(ERole.ROLE_ONG)));
    }

    // ===================== GET /api/organizacoes — autenticado =====================

    @Test
    void listarTodas_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/organizacoes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarTodas_ComUsuarioAutenticado_DeveRetornar200() throws Exception {
        when(organizacaoService.listarTodasOrganizacoes()).thenReturn(List.of(orgResponse));

        mockMvc.perform(get("/api/organizacoes")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeFantasia").value("ONG Patinhas"));
    }

    // ===================== GET /api/organizacoes/{id} — autenticado =====================

    @Test
    void buscarPorId_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/organizacoes/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void buscarPorId_ComUsuarioAutenticado_DeveRetornar200() throws Exception {
        when(organizacaoService.buscarOrganizacaoPorId(1L)).thenReturn(orgResponse);

        mockMvc.perform(get("/api/organizacoes/1")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeFantasia").value("ONG Patinhas"));
    }

    // ===================== POST /api/organizacoes — ADMIN =====================

    @Test
    void criar_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(post("/api/organizacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORG_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criar_ComRoleUser_DeveRetornar403() throws Exception {
        mockMvc.perform(post("/api/organizacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORG_JSON)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void criar_ComRoleAdmin_DeveRetornar201() throws Exception {
        when(organizacaoService.criarOrganizacao(any(OrganizacaoRequestDTO.class))).thenReturn(orgResponse);

        mockMvc.perform(post("/api/organizacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORG_JSON)
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeFantasia").value("ONG Patinhas"));
    }

    // ===================== PUT /api/organizacoes/{id} — ADMIN ou ONG =====================

    @Test
    void atualizar_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(put("/api/organizacoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORG_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void atualizar_ComRoleUser_DeveRetornar403() throws Exception {
        mockMvc.perform(put("/api/organizacoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORG_JSON)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void atualizar_ComRoleAdmin_DeveRetornar200() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(adminUser);
        when(organizacaoService.atualizarOrganizacao(eq(1L), any())).thenReturn(orgResponse);

        mockMvc.perform(put("/api/organizacoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORG_JSON)
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeFantasia").value("ONG Patinhas"));
    }

    // ===================== DELETE /api/organizacoes/{id} — ADMIN =====================

    @Test
    void deletar_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(delete("/api/organizacoes/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deletar_ComRoleOng_DeveRetornar403() throws Exception {
        mockMvc.perform(delete("/api/organizacoes/1")
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletar_ComRoleAdmin_DeveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/organizacoes/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNoContent());

        verify(organizacaoService).deletarOrganizacao(eq(1L));
    }
}
