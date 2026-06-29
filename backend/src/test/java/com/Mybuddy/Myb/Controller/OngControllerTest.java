package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.DTO.PetResponse;
import com.Mybuddy.Myb.Model.EventoOng;
import com.Mybuddy.Myb.Model.InteresseAdocao;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Model.StatusAdocao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.PetService;
import com.Mybuddy.Myb.Repository.mongo.EventoOngRepository;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OngController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class OngControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InteresseAdocaoRepository interesseAdocaoRepository;

    @MockitoBean
    private EventoOngRepository eventoOngRepository;

    @MockitoBean
    private PetService petService;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    private Usuario adminUser;
    private Usuario ongUser;
    private Pet pet;
    private PetResponse petResponse;
    private InteresseAdocao interesse;
    private EventoOng evento;

    @BeforeEach
    void setUp() {
        adminUser = new Usuario();
        adminUser.setId(1L);
        adminUser.setRoles(Set.of(new Role(ERole.ROLE_ADMIN)));

        ongUser = new Usuario();
        ongUser.setId(2L);
        ongUser.setRoles(Set.of(new Role(ERole.ROLE_ONG)));
        Organizacao org = new Organizacao();
        org.setId(10L);
        ongUser.setOrganizacao(org);

        pet = new Pet();
        pet.setId(100L);
        pet.setNome("Bidu");
        pet.setOrganizacao(org);

        petResponse = new PetResponse(100L, "Bidu", null, null, null, null, null, null, null,
                List.of(), null, StatusAdocao.DISPONIVEL, "Org", 10L, false, false, false, null, null, null);

        interesse = new InteresseAdocao();
        interesse.setId(1000L);
        interesse.setPet(pet);

        evento = new EventoOng();
        evento.setId(50L);
        evento.setNome("Feira de Adoção");
    }

    @Test
    void deveRetornar401QuandoAcessarSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/ong/solicitacoes"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/ong/pets"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/ong/eventos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoUsuarioNaoTiverRoleAutorizada() throws Exception {
        mockMvc.perform(get("/api/ong/solicitacoes")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornarSolicitacoesFiltradasParaOng() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(ongUser);
        when(interesseAdocaoRepository.findByPetOrganizacaoId(10L)).thenReturn(List.of(interesse));

        mockMvc.perform(get("/api/ong/solicitacoes")
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1000));
    }

    @Test
    void deveRetornarTodasAsSolicitacoesParaAdmin() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(adminUser);
        when(interesseAdocaoRepository.findAll()).thenReturn(List.of(interesse));

        mockMvc.perform(get("/api/ong/solicitacoes")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1000));
    }

    @Test
    void deveRetornarListaVaziaSeOngNaoTiverOrganizacaoAssociada() throws Exception {
        Usuario ongSemOrg = new Usuario();
        ongSemOrg.setId(3L);
        ongSemOrg.setRoles(Set.of(new Role(ERole.ROLE_ONG)));

        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(ongSemOrg);

        mockMvc.perform(get("/api/ong/solicitacoes")
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void deveRetornarPetsFiltradosParaOng() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(ongUser);
        when(petService.buscarPetsPorOrganizacaoId(10L)).thenReturn(List.of(petResponse));

        mockMvc.perform(get("/api/ong/pets")
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Bidu"));
    }

    @Test
    void deveRetornarTodosOsPetsParaAdmin() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(adminUser);
        when(petService.listarTodosDTO()).thenReturn(List.of(petResponse));

        mockMvc.perform(get("/api/ong/pets")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Bidu"));
    }

    @Test
    void deveRetornarEventosFiltradosParaOng() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(ongUser);
        when(eventoOngRepository.findByOrganizacaoId(10L)).thenReturn(List.of(evento));

        mockMvc.perform(get("/api/ong/eventos")
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Feira de Adoção"));
    }

    @Test
    void deveRetornarTodosOsEventosParaAdmin() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(adminUser);
        when(eventoOngRepository.findAll()).thenReturn(List.of(evento));

        mockMvc.perform(get("/api/ong/eventos")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Feira de Adoção"));
    }
}
