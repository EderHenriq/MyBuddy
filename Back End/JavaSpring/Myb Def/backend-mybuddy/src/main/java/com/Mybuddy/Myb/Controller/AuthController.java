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
        try {
            Authentication authentication = authService.authenticateUser(loginRequest);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = authService.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles,
                    userDetails.getOrganizacaoId()));

        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(401).body(new MessageResponse("Credenciais inválidas."));
        }
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