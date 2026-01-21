package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.PetRequestDTO;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.OrganizacaoRepository;
import com.Mybuddy.Myb.Repository.PetRepository;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@DisplayName("Testes de Integração - PetController")
class PetControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private String tokenOng;
    private String tokenAdmin;
    private Organizacao organizacao;
    private Usuario usuarioOng;
    private Usuario usuarioAdmin;

    @BeforeEach
    void setUp() {
        // Criar roles se não existirem
        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG).orElseGet(() -> {
            Role r = new Role();
            r.setName(ERole.ROLE_ONG);
            return roleRepository.save(r);
        });

        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseGet(() -> {
            Role r = new Role();
            r.setName(ERole.ROLE_ADMIN);
            return roleRepository.save(r);
        });

        Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE).orElseGet(() -> {
            Role r = new Role();
            r.setName(ERole.ROLE_ADOTANTE);
            return roleRepository.save(r);
        });

        // Criar organização
        organizacao = new Organizacao();
        organizacao.setNomeFantasia("ONG Teste");
        organizacao.setEmailContato("ong@test.com");
        organizacao.setCnpj("12345678000100");
        organizacao.setEndereco("Rua Teste, 123");
        organizacao = organizacaoRepository.save(organizacao);

        // Criar usuário ONG
        usuarioOng = new Usuario();
        usuarioOng.setNome("ONG User");
        usuarioOng.setEmail("ong@test.com");
        usuarioOng.setPassword(passwordEncoder.encode("senha123"));
        usuarioOng.setTelefone("11999999999");
        usuarioOng.setOrganizacao(organizacao);
        Set<Role> ongRoles = new HashSet<>();
        ongRoles.add(ongRole);
        usuarioOng.setRoles(ongRoles);
        usuarioOng = usuarioRepository.save(usuarioOng);

        // Criar usuário Admin
        usuarioAdmin = new Usuario();
        usuarioAdmin.setNome("Admin User");
        usuarioAdmin.setEmail("admin@test.com");
        usuarioAdmin.setPassword(passwordEncoder.encode("admin123"));
        usuarioAdmin.setTelefone("11988888888");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        usuarioAdmin.setRoles(adminRoles);
        usuarioAdmin = usuarioRepository.save(usuarioAdmin);

        // Gerar tokens
        tokenOng = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
                usuarioOng.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_ONG"))));

        tokenAdmin = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
                usuarioAdmin.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @Test
    @DisplayName("POST /api/pets - ONG deve criar pet com sucesso")
    void criarPet_ComoOng_RetornaCriado() throws Exception {
        // Arrange
        PetRequestDTO request = new PetRequestDTO();
        request.setNome("Rex");
        request.setEspecie(Especie.CAO);
        request.setRaca("Labrador");
        request.setIdade(3);
        request.setPorte(Porte.MEDIO);
        request.setCor("Dourado");
        request.setPelagem("Curto");
        request.setSexo("Macho");
        request.setMicrochipado(true);
        request.setVacinado(true);
        request.setCastrado(true);
        request.setCidade("São Paulo");
        request.setEstado("SP");
        request.setOrganizacaoId(organizacao.getId());

        // Act & Assert
        mockMvc.perform(post("/api/pets")
                        .header("Authorization", "Bearer " + tokenOng)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Rex"))
                .andExpect(jsonPath("$.especie").value("CAO"))
                .andExpect(jsonPath("$.porte").value("MEDIO"));
    }

    @Test
    @DisplayName("GET /api/pets - Deve listar pets com autenticação")
    void buscarPets_Autenticado_RetornaLista() throws Exception {
        // Arrange - Criar um pet primeiro
        Pet pet = new Pet();
        pet.setNome("Rex");
        pet.setEspecie(Especie.CAO);
        pet.setRaca("Labrador");
        pet.setIdade(3);
        pet.setPorte(Porte.MEDIO);
        pet.setCor("Dourado");
        pet.setSexo("Macho");
        pet.setStatusAdocao(StatusAdocao.DISPONIVEL);
        pet.setOrganizacao(organizacao);
        petRepository.save(pet);

        // Act & Assert
        mockMvc.perform(get("/api/pets")
                        .header("Authorization", "Bearer " + tokenOng)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/pets/{id} - Deve retornar pet existente")
    void buscarPetPorId_PetExiste_RetornaPet() throws Exception {
        // Arrange
        Pet pet = new Pet();
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

        // Act & Assert
        mockMvc.perform(get("/api/pets/" + pet.getId())
                        .header("Authorization", "Bearer " + tokenOng))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Rex"))
                .andExpect(jsonPath("$.id").value(pet.getId()));
    }

    @Test
    @DisplayName("GET /api/pets/{id} - Deve retornar 404 para pet inexistente")
    void buscarPetPorId_PetNaoExiste_Retorna404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/pets/99999")
                        .header("Authorization", "Bearer " + tokenOng))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/pets/{id} - Admin deve deletar pet")
    void deletarPet_ComoAdmin_DeletaComSucesso() throws Exception {
        // Arrange
        Pet pet = new Pet();
        pet.setNome("Rex");
        pet.setEspecie(Especie.CAO);
        pet.setStatusAdocao(StatusAdocao.DISPONIVEL);
        pet.setOrganizacao(organizacao);
        pet = petRepository.save(pet);

        // Act & Assert
        mockMvc.perform(delete("/api/pets/" + pet.getId())
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }
}
