package com.Mybuddy.Myb.Controller;

// Importações dos modelos, payloads e utilitários necessários
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Payload.Request.LoginRequest;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Payload.Response.JwtResponse;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
import com.Mybuddy.Myb.Security.Role; // Entidade que representa um papel (role) no sistema
import com.Mybuddy.Myb.Security.ERole; // Enum que define os tipos de roles disponíveis
import com.Mybuddy.Myb.Security.jwt.JwtUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.*;
import java.util.stream.Collectors;

// Define que esta classe é um controlador REST do Spring Boot
@RestController
// Define o caminho base para todos os endpoints deste controlador
@RequestMapping("/api/auth")
public class AuthController {

    // --- Injeções de dependência ---
    private final AuthenticationManager authenticationManager; // Gerencia a autenticação do Spring Security
    private final UsuarioRepository userRepository; // Acesso ao banco de dados dos usuários
    private final RoleRepository roleRepository; // Acesso ao banco de dados das roles
    private final PasswordEncoder encoder; // Responsável por codificar senhas
    private final JwtUtils jwtUtils; // Utilitário para geração e validação de tokens JWT

    // Construtor para injeção de dependências via construtor
    public AuthController(AuthenticationManager authenticationManager, UsuarioRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    // --- Endpoint de LOGIN ---
    // Este método autentica o usuário e gera um token JWT
    @PostMapping("/login") // URL: /api/auth/login
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Cria um objeto de autenticação com email e telefone (usado como senha)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getTelefone()));

        // Define o contexto de segurança atual com o usuário autenticado
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Gera o token JWT para este usuário autenticado
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Obtém os detalhes do usuário autenticado
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Extrai as roles (perfis) do usuário e transforma em lista de strings
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Retorna a resposta com os dados do usuário e o token JWT
        // O objeto JwtResponse encapsula todas as informações do login
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),          // ID do usuário
                userDetails.getUsername(),    // Nome de usuário (ou email)
                userDetails.getNome(),        // Nome completo (caso exista no UserDetailsImpl)
                roles));                      // Lista de roles (ex: ROLE_ADMIN, ROLE_ONG, etc.)
    }

    // --- Endpoint de CADASTRO ---
    // Este método registra um novo usuário e atribui roles (perfis)
    @PostMapping("/cadastro") // URL: /api/auth/cadastro
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        // Cria uma nova instância de usuário com os dados recebidos
        // O telefone é usado como senha e é codificado com BCrypt
        Usuario user = new Usuario(signUpRequest.getNome(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getTelefone()));

        // Recupera as roles enviadas no corpo da requisição
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        // Caso o usuário não especifique uma role, define uma padrão (ADOTANTE)
        if (strRoles == null || strRoles.isEmpty()) {
            Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                    .orElseThrow(() -> new RuntimeException("Erro: Role ADOTANTE não encontrada."));
            roles.add(adotanteRole);
        } else {
            // Caso o cliente envie uma role, verifica e associa a correspondente
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin": // Caso o usuário seja administrador
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erro: Role ADMIN não encontrada."));
                        roles.add(adminRole);
                        break;

                    case "ong": // Caso o usuário seja uma ONG
                        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG)
                                .orElseThrow(() -> new RuntimeException("Erro: Role ONG não encontrada."));
                        roles.add(ongRole);
                        break;

                    // Caso padrão: se não for admin nem ONG, será adotante
                    case "adotante":
                    default:
                        Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                                .orElseThrow(() -> new RuntimeException("Erro: Role ADOTANTE não encontrada."));
                        roles.add(adotanteRole);
                }
            });
        }

        // Define as roles do usuário e salva no banco de dados
        user.setRoles(roles);
        userRepository.save(user);

        // Retorna uma mensagem simples de sucesso
        Map<String, String> responseBody = Collections.singletonMap("message", "Usuário registrado com sucesso!");
        return ResponseEntity.ok(responseBody);
    }
}
