package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Payload.Response.MessageResponse;
import com.Mybuddy.Myb.Service.AuthService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    @PostMapping("/cadastro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));
    }
}
