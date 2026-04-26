package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Payload.Response.MessageResponse;
import com.Mybuddy.Myb.Service.AuthService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    public AuthController(AuthService authService, KeycloakUserSyncService keycloakUserSyncService) {
        this.authService = authService;
        this.keycloakUserSyncService = keycloakUserSyncService;
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
            authService.registerUser(signUpRequest);
            return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}