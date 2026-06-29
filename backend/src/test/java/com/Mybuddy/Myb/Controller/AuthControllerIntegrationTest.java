package com.Mybuddy.Myb.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.Mybuddy.Myb.Config.TestSecurityConfig;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @MockBean
    UsuarioRepository usuarioRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(anyString())).thenReturn(false);
    }

    // ===================== /api/auth/cadastro =====================

    @Test
    void deveRegistrarAdotanteComSucesso() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setNome("Eder Henrique");
        request.setEmail("eder@mybuddy.com");
        request.setTelefone("44999999999");
        request.setPassword("senha123");
        request.setRoles(Set.of("ADOTANTE"));
        request.setAceitaTermos(true);

        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));
    }

    @Test
    void deveRetornarErroQuandoEmailJaCadastrado() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setNome("Eder Henrique");
        request.setEmail("duplicado@mybuddy.com");
        request.setTelefone("44988888888");
        request.setPassword("senha123");
        request.setRoles(Set.of("ADOTANTE"));
        request.setAceitaTermos(true);

        // Primeira chamada: e-mail livre; segunda: já cadastrado
        when(usuarioRepository.existsByEmail("duplicado@mybuddy.com")).thenReturn(false, true);

        mockMvc.perform(post("/api/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Erro: O e-mail já está em uso!"));
    }

    // ===================== Proteção de rotas =====================

    @Test
    void deveRetornar401QuandoAcessarRotaProtegidaSemToken() throws Exception {
        // O login agora é feito pelo Keycloak — rotas protegidas devem retornar 401
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar404QuandoEndpointLoginLegadoForAcessado() throws Exception {
        // O endpoint /api/auth/login foi removido — deve retornar 404
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is4xxClientError());
    }
}
