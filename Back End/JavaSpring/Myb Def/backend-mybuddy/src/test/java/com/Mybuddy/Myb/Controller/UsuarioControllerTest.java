package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.FotoPetService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    @MockitoBean
    private FotoPetService fotoPetService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setTelefone("11999999999");
        usuario.setUrlAvatar("/uploads/avatar.png");
    }

    @Test
    void deveRetornarMeuPerfilComSucesso() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);

        mockMvc.perform(get("/api/usuarios/meu-perfil")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));
    }

    @Test
    void deveAtualizarMeuPerfilComSucesso() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(usuarioService.atualizarUsuario(eq(1L), any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(1));

        String requestBody = """
                {
                    "nome": "João Silva Alterado",
                    "telefone": "11988888888"
                }
                """;

        mockMvc.perform(put("/api/usuarios/meu-perfil")
                        .with(jwt().authorities(() -> "ROLE_USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Alterado"))
                .andExpect(jsonPath("$.telefone").value("11988888888"));
    }

    @Test
    void deveFazerUploadDeAvatarComSucesso() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "some image bytes".getBytes());
        when(fotoPetService.storeFile(any())).thenReturn("stored-image.png");
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);

        mockMvc.perform(multipart("/api/usuarios/meu-perfil/avatar")
                        .file(file)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("/uploads/stored-image.png"));

        verify(usuarioService).atualizarUsuario(eq(1L), any(Usuario.class));
    }

    @Test
    void deveRetornarBadRequestQuandoArquivoDeAvatarForVazio() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "", "image/png", new byte[0]);

        mockMvc.perform(multipart("/api/usuarios/meu-perfil/avatar")
                        .file(file)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Por favor selecione um arquivo."));
    }

    @Test
    void deveRetornar403ParaBuscarUsuariosSemRoleAdmin() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarUsuariosQuandoAdmin() throws Exception {
        when(usuarioService.buscarTodosUsuarios()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuarios")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    void deveCriarUsuarioQuandoAdmin() throws Exception {
        when(usuarioService.criarUsuario(any(Usuario.class))).thenReturn(usuario);

        String requestBody = """
                {
                    "nome": "João Silva",
                    "email": "joao@example.com",
                    "telefone": "11999999999"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void deveBuscarUsuarioPorIdQuandoAdmin() throws Exception {
        when(usuarioService.buscarUsuarioPorId(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void deveRetornar404AoBuscarUsuarioPorIdInexistente() throws Exception {
        when(usuarioService.buscarUsuarioPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarUsuarioQuandoAdmin() throws Exception {
        when(usuarioService.atualizarUsuario(eq(1L), any(Usuario.class))).thenReturn(usuario);

        String requestBody = """
                {
                    "nome": "João Silva",
                    "email": "joao@example.com",
                    "telefone": "11999999999"
                }
                """;

        mockMvc.perform(put("/api/usuarios/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void deveDeletarUsuarioQuandoAdmin() throws Exception {
        doNothing().when(usuarioService).deletarUsuario(1L);

        mockMvc.perform(delete("/api/usuarios/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNoContent());
    }
}
