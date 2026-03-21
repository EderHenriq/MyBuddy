package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Organizacao; // Manter, pois JwtResponse pode precisar
import com.Mybuddy.Myb.Payload.Request.LoginRequest;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Payload.Response.JwtResponse;
import com.Mybuddy.Myb.Payload.Response.MessageResponse; // NOVO: Classe para respostas simples
import com.Mybuddy.Myb.Service.AuthService; // NOVO: Injeta o AuthService
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Apenas Autowire o AuthService e o AuthenticationManager diretamente.
    // O AuthService encapsulará os outros repositórios e o PasswordEncoder.
    private final AuthService authService; // NOVO: Injeta o AuthService

    public AuthController(AuthService authService) { // Construtor com injeção de AuthService
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // A lógica de autenticação pode ser movida para AuthService ou mantida aqui,
        // dependendo da granularidade que você quer dar ao AuthService.
        // Por simplicidade, vou manter a autenticação aqui e ajustar a resposta de login.
        Authentication authentication = authService.authenticateUser(loginRequest); // Delega para AuthService

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = authService.generateJwtToken(authentication); // Delega geração de JWT

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Agora, se o userDetails tiver um ID de organização, ele será incluído no JwtResponse
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(), // Geralmente o email ou um nome de usuário único
                userDetails.getEmail(),
                roles,
                userDetails.getOrganizacaoId())); // Inclui o ID da organização
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            authService.registerUser(signUpRequest); // Delega toda a lógica para AuthService
            return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));
        } catch (RuntimeException e) { // Captura exceções de validação ou conflito do serviço
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}