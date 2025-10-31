package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Payload.Request.LoginRequest;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Payload.Response.JwtResponse;
import com.Mybuddy.Myb.Repository.OrganizacaoRepository;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.ERole;
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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final OrganizacaoRepository organizacaoRepository;

    public AuthController(AuthenticationManager authenticationManager, UsuarioRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils,
                          OrganizacaoRepository organizacaoRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.organizacaoRepository = organizacaoRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getNome(),
                roles));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap("message", "Erro: O e-mail já está em uso!"));
        }

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        Organizacao organizacaoAssociada = null; // Inicializada como null e será atribuída se necessário

        // --- Lógica de atribuição de Roles e Organização ---
        if (strRoles == null || strRoles.isEmpty()) {
            Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                    .orElseThrow(() -> new RuntimeException("Erro: Role ADOTANTE não encontrada."));
            roles.add(adotanteRole);
        } else {
            // Se houver roles especificadas, processá-las
            for (String roleName : strRoles) { // Usando um loop for-each tradicional para maior flexibilidade
                switch (roleName) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erro: Role ADMIN não encontrada."));
                        roles.add(adminRole);
                        break;

                    case "ong":
                        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG)
                                .orElseThrow(() -> new RuntimeException("Erro: Role ONG não encontrada."));
                        roles.add(ongRole);

                        // Lógica de associação da Organização
                        if (signUpRequest.getOrganizacaoId() != null) {
                            organizacaoAssociada = organizacaoRepository.findById(signUpRequest.getOrganizacaoId())
                                    .orElseThrow(() -> new RuntimeException("Erro: Organização não encontrada com ID: " + signUpRequest.getOrganizacaoId()));
                        } else if (signUpRequest.getOrganizacaoCnpj() != null) {
                            organizacaoAssociada = organizacaoRepository.findByCnpj(signUpRequest.getOrganizacaoCnpj())
                                    .orElseThrow(() -> new RuntimeException("Erro: Organização com CNPJ " + signUpRequest.getOrganizacaoCnpj() + " não encontrada. Por favor, crie a ONG primeiro ou forneça um ID."));
                        } else {
                            throw new RuntimeException("Erro: Usuário ONG deve fornecer ID ou CNPJ da organização.");
                        }
                        break; // Importante para sair do switch após processar a role "ong"

                    case "adotante":
                    default:
                        Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                                .orElseThrow(() -> new RuntimeException("Erro: Role ADOTANTE não encontrada."));
                        roles.add(adotanteRole);
                }
            }
        }

        Usuario user = new Usuario(
                signUpRequest.getNome(),
                signUpRequest.getEmail(),
                signUpRequest.getTelefone(),
                encoder.encode(signUpRequest.getPassword()),
                organizacaoAssociada, // Associa a organização (pode ser null)
                roles
        );

        userRepository.save(user);

        Map<String, String> responseBody = Collections.singletonMap("message", "Usuário registrado com sucesso!");
        return ResponseEntity.ok(responseBody);
    }
}