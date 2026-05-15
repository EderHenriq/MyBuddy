package com.Mybuddy.Myb.Controller;

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
