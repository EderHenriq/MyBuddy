package com.Mybuddy.Myb.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.Mybuddy.Myb.Payload.Request.LoginRequest;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ===================== /api/auth/cadastro =====================

    @Test
    void deveRegistrarAdotanteComSucesso() throws Exception {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setNome("Eder Henrique");
        request.setEmail("eder@mybuddy.com");
        request.setTelefone("44999999999");
        request.setPassword("senha123");
        request.setRoles(Set.of("ADOTANTE"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));
    }

    @Test
    void deveRetornarErroQuandoEmailJaCadastrado() throws Exception {
        // Arrange — cadastra o primeiro usuário
        SignupRequest request = new SignupRequest();
        request.setNome("Eder Henrique");
        request.setEmail("duplicado@mybuddy.com");
        request.setTelefone("44988888888");
        request.setPassword("senha123");
        request.setRoles(Set.of("ADOTANTE"));

        mockMvc.perform(post("/api/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Act & Assert — tenta cadastrar de novo com o mesmo e-mail
        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro: O e-mail já está em uso!"));
    }

    // ===================== /api/auth/login =====================

    @Test
    void deveLogarComSucesso() throws Exception {
        // Arrange — cadastra o usuário primeiro
        SignupRequest signup = new SignupRequest();
        signup.setNome("Eder Henrique");
        signup.setEmail("login@mybuddy.com");
        signup.setTelefone("44977777777");
        signup.setPassword("senha123");
        signup.setRoles(Set.of("ADOTANTE"));

        mockMvc.perform(post("/api/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        // Act & Assert — faz login
        LoginRequest login = new LoginRequest();
        login.setEmail("login@mybuddy.com");
        login.setPassword("senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.email").value("login@mybuddy.com"));
    }

    @Test
    void deveRetornarErroQuandoSenhaErrada() throws Exception {
        // Arrange — cadastra o usuário
        SignupRequest signup = new SignupRequest();
        signup.setNome("Eder Henrique");
        signup.setEmail("senhaerrada@mybuddy.com");
        signup.setTelefone("44966666666");
        signup.setPassword("senha123");
        signup.setRoles(Set.of("ADOTANTE"));

        mockMvc.perform(post("/api/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        // Act & Assert — tenta logar com senha errada
        LoginRequest login = new LoginRequest();
        login.setEmail("senhaerrada@mybuddy.com");
        login.setPassword("senhaErrada!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}