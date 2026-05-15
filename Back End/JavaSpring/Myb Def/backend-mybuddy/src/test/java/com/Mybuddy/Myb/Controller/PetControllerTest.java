package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.DTO.PetRequestDTO;
import com.Mybuddy.Myb.DTO.PetResponse;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.FotoPetService;
import com.Mybuddy.Myb.Service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @MockBean
    private FotoPetService fotoPetService;

    @Test
    void deveRetornar401QuandoSemToken() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar200QuandoAutenticado() throws Exception {
        when(petService.buscarComFiltrosDTO(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/pets")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar403QuandoUserTentaCriarPet() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
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
                                """)
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar201QuandoOngCriaPet() throws Exception {
        PetResponse petResponse = new PetResponse(
                1L, "Rex", "CAO", "Labrador", 2, "GRANDE", "Amarelo",
                null, "M", List.of(), "Em Adoção", null, "ONG Teste",
                1L, false, true, true, null, null
        );

        when(petService.criarPet(any(PetRequestDTO.class))).thenReturn(petResponse);

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
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
                                """)
                        .with(jwt().authorities(() -> "ROLE_ONG")))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar204QuandoAdminDeletaPet() throws Exception {
        mockMvc.perform(delete("/api/pets/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar403QuandoUserTentaDeletarPet() throws Exception {
        mockMvc.perform(delete("/api/pets/1")
                        .with(jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }
}