package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.*;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@DisplayName("Testes de Integração - InteresseAdocaoController")
class InteresseAdocaoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InteresseAdoacaoRepository interesseRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private String tokenAdotante;
    private String tokenOng;
    private Usuario usuarioAdotante;
    private Usuario usuarioOng;
    private Pet pet;
    private Organizacao organizacao;

    @BeforeEach
    void setUp() {
        // Criar roles
        Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE).orElseGet(() -> {
            Role r = new Role();
            r.setName(ERole.ROLE_ADOTANTE);
            return roleRepository.save(r);
        });

        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG).orElseGet(() -> {
            Role r = new Role();
            r.setName(ERole.ROLE_ONG);
            return roleRepository.save(r);
        });

        // Criar organização
        organizacao = new Organizacao();
        organizacao.setNomeFantasia("ONG Teste");
        organizacao.setEmailContato("ong@test.com");
        organizacao.setCnpj("12345678000100");
        organizacao.setEndereco("Rua Teste, 123");
        organizacao = organizacaoRepository.save(organizacao);

        // Criar usuário adotante
        usuarioAdotante = new Usuario();
        usuarioAdotante.setNome("Adotante User");
        usuarioAdotante.setEmail("adotante@test.com");
        usuarioAdotante.setPassword(passwordEncoder.encode("senha123"));
        usuarioAdotante.setTelefone("11999999999");
        Set<Role> adotanteRoles = new HashSet<>();
        adotanteRoles.add(adotanteRole);
        usuarioAdotante.setRoles(adotanteRoles);
        usuarioAdotante = usuarioRepository.save(usuarioAdotante);

        // Criar usuário ONG
        usuarioOng = new Usuario();
        usuarioOng.setNome("ONG User");
        usuarioOng.setEmail("ong@test.com");
        usuarioOng.setPassword(passwordEncoder.encode("senha123"));
        usuarioOng.setTelefone("11988888888");
        usuarioOng.setOrganizacao(organizacao);
        Set<Role> ongRoles = new HashSet<>();
        ongRoles.add(ongRole);
        usuarioOng.setRoles(ongRoles);
        usuarioOng = usuarioRepository.save(usuarioOng);

        // Criar pet disponível
        pet = new Pet();
        pet.setNome("Rex");
        pet.setEspecie(Especie.CAO);
        pet.setRaca("Labrador");
        pet.setIdade(3);
        pet.setPorte(Porte.MEDIO);
        pet.setCor("Dourado");
        pet.setSexo("Macho");
        pet.setStatusAdocao(StatusAdocao.DISPONIVEL);
        pet.setOrganizacao(organizacao);
        pet = petRepository.save(pet);

        // Gerar tokens
        tokenAdotante = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
                usuarioAdotante.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_ADOTANTE"))));

        tokenOng = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
                usuarioOng.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_ONG"))));
    }

    @Test
    @DisplayName("POST /api/interesses - Deve registrar interesse com sucesso")
    void registrarInteresse_ComDadosValidos_RetornaCriado() throws Exception {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("petId", pet.getId());
        request.put("mensagem", "Gostaria de adotar este pet");

        // Act & Assert
        mockMvc.perform(post("/api/interesses")
                        .header("Authorization", "Bearer " + tokenAdotante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    @DisplayName("PUT /api/interesses/{id}/status - ONG deve aprovar interesse")
    void atualizarStatus_OngAprova_RetornaAprovado() throws Exception {
        // Arrange - Criar interesse primeiro
        InteresseAdocao interesse = new InteresseAdocao();
        interesse.setUsuario(usuarioAdotante);
        interesse.setPet(pet);
        interesse.setMensagem("Quero adotar");
        interesse.setStatus(StatusInteresse.PENDENTE);
        interesse = interesseRepository.save(interesse);

        Map<String, String> request = new HashMap<>();
        request.put("novoStatus", "APROVADO");

        // Act & Assert
        mockMvc.perform(put("/api/interesses/" + interesse.getId() + "/status")
                        .header("Authorization", "Bearer " + tokenOng)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));
    }

    @Test
    @DisplayName("GET /api/usuarios/me/interesses - Deve listar meus interesses")
    void listarMeusInteresses_ComoAdotante_RetornaLista() throws Exception {
        // Arrange - Criar interesse primeiro
        InteresseAdocao interesse = new InteresseAdocao();
        interesse.setUsuario(usuarioAdotante);
        interesse.setPet(pet);
        interesse.setMensagem("Quero adotar");
        interesse.setStatus(StatusInteresse.PENDENTE);
        interesseRepository.save(interesse);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/me/interesses")
                        .header("Authorization", "Bearer " + tokenAdotante))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("PENDENTE"));
    }

    @Test
    @DisplayName("GET /api/ongs/me/interesses - ONG deve ver interesses recebidos")
    void listarInteressesOng_ComoOng_RetornaLista() throws Exception {
        // Arrange - Criar interesse primeiro
        InteresseAdocao interesse = new InteresseAdocao();
        interesse.setUsuario(usuarioAdotante);
        interesse.setPet(pet);
        interesse.setMensagem("Quero adotar");
        interesse.setStatus(StatusInteresse.PENDENTE);
        interesseRepository.save(interesse);

        // Act & Assert
        mockMvc.perform(get("/api/ongs/me/interesses")
                        .header("Authorization", "Bearer " + tokenOng))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
