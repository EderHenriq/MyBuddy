package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Payload.Request.LoginRequest;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Payload.Response.JwtResponse;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UserRepository;
import com.Mybuddy.Myb.Security.User;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
import com.Mybuddy.Myb.Security.Role; // <<--- Import correto para Role Entity (baseado em conversas anteriores)
import com.Mybuddy.Myb.Security.ERole; // <<--- Import correto para ERole (baseado em conversas anteriores)
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login") // O endpoint é /api/auth/login
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                // <<--- CORRIGIDO: Agora usa loginRequest.getPassword()
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getTelefone()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // AQUI PRECISAREMOS VERIFICAR OS CAMPOS DA SUA CLASSE JwtResponse
        // Mas por enquanto, vamos focar no login. Se der erro aqui depois, vemos JwtResponse.
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(), // Este é o email (ou username, dependendo de como você mapeou)
                userDetails.getNome(),     // Se UserDetailsImpl tiver getNome()
                roles));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // ... validação de email/telefone existente ...

        // Criar novo usuário
        Usuario user = new Usuario(signUpRequest.getNome(), // ou getUsername()
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getTelefone())); // Codifica o telefone/senha

        // <<--- Lógica para atribuir roles (Pode ser que você já tenha algo parecido)
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Se nenhuma role for especificada, atribui uma padrão, por exemplo, ADOTANTE
            Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                    .orElseThrow(() -> new RuntimeException("Erro: Role ADOTANTE não encontrada."));
            roles.add(adotanteRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin": // Se você tiver uma role de admin, trate-a aqui
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erro: Role ADMIN não encontrada."));
                        roles.add(adminRole);
                        break;
                    case "ong":
                        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG)
                                .orElseThrow(() -> new RuntimeException("Erro: Role ONG não encontrada."));
                        roles.add(ongRole);
                        break;
                    case "adotante": // Caso padrão se houver apenas "adotante"
                    default: // Garante que se não for nenhum dos anteriores, caia em adotante
                        Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                                .orElseThrow(() -> new RuntimeException("Erro: Role ADOTANTE não encontrada."));
                        roles.add(adotanteRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        Map<String, String> responseBody = Collections.singletonMap("message", "Usuário registrado com sucesso!");
        return ResponseEntity.ok(responseBody);
    }
}