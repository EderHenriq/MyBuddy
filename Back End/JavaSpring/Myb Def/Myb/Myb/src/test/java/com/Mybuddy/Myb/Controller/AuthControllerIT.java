package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Payload.Request.LoginRequest;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@DisplayName("Testes de Integração - AuthController")
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Certifica que as roles existem no banco
        if (roleRepository.findByName(ERole.ROLE_ADOTANTE).isEmpty()) {
            Role role = new Role();
            role.setName(ERole.ROLE_ADOTANTE);
            roleRepository.save(role);
        }
        if (roleRepository.findByName(ERole.ROLE_ONG).isEmpty()) {
            Role role = new Role();
            role.setName(ERole.ROLE_ONG);
            roleRepository.save(role);
        }
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            Role role = new Role();
            role.setName(ERole.ROLE_ADMIN);
            roleRepository.save(role);
        }
    }

    @Test
    @DisplayName("POST /api/auth/cadastro - Deve cadastrar usuário com sucesso")
    void cadastro_ComDadosValidos_RetornaSucesso() throws Exception {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setNome("João Silva");
        request.setEmail("joao@test.com");
        request.setTelefone("11999999999");
        request.setPassword("senha123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));
    }

    @Test
    @DisplayName("POST /api/auth/cadastro - Deve retornar erro quando email duplicado")
    void cadastro_EmailDuplicado_RetornaErro() throws Exception {
        // Arrange - Primeiro cadastro
        SignupRequest request1 = new SignupRequest();
        request1.setNome("João Silva");
        request1.setEmail("joao@test.com");
        request1.setTelefone("11999999999");
        request1.setPassword("senha123");

        mockMvc.perform(post("/api/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));

        // Arrange - Segundo cadastro com mesmo email
        SignupRequest request2 = new SignupRequest();
        request2.setNome("Maria Silva");
        request2.setEmail("joao@test.com");
        request2.setTelefone("11988888888");
        request2.setPassword("senha456");

        // Act & Assert
        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("e-mail")));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve autenticar com credenciais válidas")
    void login_CredenciaisValidas_RetornaToken() throws Exception {
        // Arrange - Criar usuário primeiro
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setNome("João Silva");
        signupRequest.setEmail("joao@test.com");
        signupRequest.setTelefone("11999999999");
        signupRequest.setPassword("senha123");

        mockMvc.perform(post("/api/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        // Arrange - Preparar login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("joao@test.com");
        loginRequest.setPassword("senha123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("joao@test.com"))
                .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar erro com senha incorreta")
    void login_SenhaIncorreta_RetornaErro() throws Exception {
        // Arrange - Criar usuário primeiro
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setNome("João Silva");
        signupRequest.setEmail("joao@test.com");
        signupRequest.setTelefone("11999999999");
        signupRequest.setPassword("senha123");

        mockMvc.perform(post("/api/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        // Arrange - Preparar login com senha errada
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("joao@test.com");
        loginRequest.setPassword("senhaErrada");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
