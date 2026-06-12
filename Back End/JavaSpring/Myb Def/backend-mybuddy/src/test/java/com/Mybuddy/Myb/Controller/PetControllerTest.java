package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.DTO.PetRequestDTO;
import com.Mybuddy.Myb.DTO.PetResponse;
import com.Mybuddy.Myb.Model.StatusAdocao;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.FotoPetService;
import com.Mybuddy.Myb.Service.PetService;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

@WebMvcTest(PetController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PetService petService;

    @MockitoBean
    private FotoPetService fotoPetService;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    private PetResponse petResponse;
    private Usuario adminUser;
    private Usuario ongUser;

    private static final String PET_JSON = """
            {
                "nome": "Rex",
                "especie": "CAO",
                "raca": "Labrador",
                "idade": 2,
                "porte": "GRANDE",
                "cor": "Amarelo",
                "sexo": "M",
                "castrado": true,
                "vacinado": true,
                "microchipado": false,
                "statusAdocao": "DISPONIVEL",
                "organizacaoId": 1
            }
            """;

    @BeforeEach
    void setUp() {
        petResponse = new PetResponse(
                1L, "Rex", "CAO", "Labrador", 2, "GRANDE", "Amarelo",
                null, "M", List.of(), "Disponível para Adoção",
                StatusAdocao.DISPONIVEL, "ONG Teste", 1L,
                false, true, true, null, null, "Rex é um cão amigável"
        );

        adminUser = new Usuario();
        adminUser.setId(1L);
        adminUser.setRoles(Set.of(new Role(ERole.ROLE_ADMIN)));

        ongUser = new Usuario();
        ongUser.setId(2L);
        Organizacao org = new Organizacao();
        org.setId(1L);
        ongUser.setOrganizacao(org);
        ongUser.setRoles(Set.of(new Role(ERole.ROLE_ONG)));
    }

    // ===================== GET /api/pets =====================

    @Test
    void deveRetornar401QuandoBuscarPetsSemToken() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar200QuandoBuscarPetsAutenticado() throws Exception {
        when(petService.buscarComFiltrosDTO(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(petResponse)));

        mockMvc.perform(get("/api/pets")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("Rex"));
    }

    @Test
    void deveRetornar200ComListaVaziaQuandoNaoHouverPets() throws Exception {
        when(petService.buscarComFiltrosDTO(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/pets")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    // ===================== GET /api/pets/{id} =====================

    @Test
    void deveRetornar401QuandoBuscarPetPorIdSemToken() throws Exception {
        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar200QuandoBuscarPetPorIdExistente() throws Exception {
        when(petService.buscarPetPorIdDTO(1L)).thenReturn(Optional.of(petResponse));

        mockMvc.perform(get("/api/pets/1")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Rex"))
                .andExpect(jsonPath("$.especie").value("CAO"));
    }

    @Test
    void deveRetornar404QuandoBuscarPetPorIdInexistente() throws Exception {
        when(petService.buscarPetPorIdDTO(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pets/99")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isNotFound());
    }

    // ===================== POST /api/pets =====================

    @Test
    void deveRetornar401QuandoCriarPetSemToken() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoUserTentaCriarPet() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar201QuandoOngCriaPet() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(ongUser);
        when(petService.criarPet(any(PetRequestDTO.class))).thenReturn(petResponse);

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON)
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Rex"))
                .andExpect(header().string("Location", "/api/pets/1"));
    }

    @Test
    void deveRetornar201QuandoAdminCriaPet() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(adminUser);
        when(petService.criarPet(any(PetRequestDTO.class))).thenReturn(petResponse);

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON)
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isCreated());
    }

    // ===================== PUT /api/pets/{id} =====================

    @Test
    void deveRetornar401QuandoAtualizarPetSemToken() throws Exception {
        mockMvc.perform(put("/api/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoUserTentaAtualizarPet() throws Exception {
        mockMvc.perform(put("/api/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar200QuandoOngAtualizaPet() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(ongUser);
        when(petService.isPetOwnedByCurrentUser(eq(1L), eq(1L))).thenReturn(true);
        when(petService.atualizarPet(eq(1L), any(PetRequestDTO.class))).thenReturn(petResponse);

        mockMvc.perform(put("/api/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON)
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Rex"));
    }

    @Test
    void deveRetornar200QuandoAdminAtualizaPet() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(adminUser);
        when(petService.atualizarPet(eq(1L), any(PetRequestDTO.class))).thenReturn(petResponse);

        mockMvc.perform(put("/api/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON)
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk());
    }

    // ===================== DELETE /api/pets/{id} =====================

    @Test
    void deveRetornar401QuandoDeletarPetSemToken() throws Exception {
        mockMvc.perform(delete("/api/pets/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoUserTentaDeletarPet() throws Exception {
        mockMvc.perform(delete("/api/pets/1")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar403QuandoOngTentaDeletarPet() throws Exception {
        mockMvc.perform(delete("/api/pets/1")
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar204QuandoAdminDeletaPet() throws Exception {
        mockMvc.perform(delete("/api/pets/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNoContent());

        verify(petService, times(1)).deletarPet(1L);
    }

    // ===================== GET /api/pets/organizacao/{id} =====================

    @Test
    void deveRetornar401QuandoBuscarPetsPorOrganizacaoSemToken() throws Exception {
        mockMvc.perform(get("/api/pets/organizacao/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoUserBuscaPetsPorOrganizacao() throws Exception {
        mockMvc.perform(get("/api/pets/organizacao/1")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar200QuandoOngBuscaPetsPorOrganizacao() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(ongUser);
        when(petService.buscarPetsPorOrganizacaoId(1L)).thenReturn(List.of(petResponse));

        mockMvc.perform(get("/api/pets/organizacao/1")
                        .with(jwt()
                                .authorities(() -> "ROLE_ONG")
                                .jwt(jwt -> jwt.subject("keycloak-id-123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Rex"))
                .andExpect(jsonPath("$[0].organizacaoId").value(1));
    }

    @Test
    void deveRetornar200QuandoAdminBuscaPetsPorOrganizacao() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(adminUser);
        when(petService.buscarPetsPorOrganizacaoId(1L)).thenReturn(List.of(petResponse));

        mockMvc.perform(get("/api/pets/organizacao/1")
                        .with(jwt()
                                .authorities(() -> "ROLE_ADMIN")
                                .jwt(jwt -> jwt.subject("keycloak-id-admin"))))
                .andExpect(status().isOk());
    }

    // ===================== POST /api/pets/upload-image =====================

    @Test
    void deveRetornar401QuandoUploadImagemSemToken() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "foto.jpg", "image/jpeg", "conteudo".getBytes()
        );

        mockMvc.perform(multipart("/api/pets/upload-image").file(file))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoUserTentaFazerUploadImagem() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "foto.jpg", "image/jpeg", "conteudo".getBytes()
        );

        mockMvc.perform(multipart("/api/pets/upload-image")
                        .file(file)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar200QuandoOngFazUploadImagem() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "foto.jpg", "image/jpeg", "conteudo".getBytes()
        );

        when(fotoPetService.storeFile(any())).thenReturn("foto.jpg");

        mockMvc.perform(multipart("/api/pets/upload-image")
                        .file(file)
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isOk())
                .andExpect(content().string("foto.jpg"));
    }

    // ===================== POST /api/pets/upload-images =====================

    @Test
    void deveRetornar200QuandoOngFazUploadMultiplasImagens() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
                "files", "foto1.jpg", "image/jpeg", "conteudo1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "foto2.jpg", "image/jpeg", "conteudo2".getBytes()
        );

        when(fotoPetService.storeFiles(any())).thenReturn(List.of("foto1.jpg", "foto2.jpg"));

        mockMvc.perform(multipart("/api/pets/upload-images")
                        .file(file1)
                        .file(file2)
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("foto1.jpg"))
                .andExpect(jsonPath("$[1]").value("foto2.jpg"));
    }

    @Test
    void deveRetornar403QuandoUserTentaFazerUploadMultiplasImagens() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files", "foto.jpg", "image/jpeg", "conteudo".getBytes()
        );

        mockMvc.perform(multipart("/api/pets/upload-images")
                        .file(file)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }
}