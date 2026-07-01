package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Payload.Response.MessageResponse;
import com.Mybuddy.Myb.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuthController {

    private final AuthService authService;

    /**
     * Registra um novo usuário no sistema.
     *
     * @param signUpRequest dados de cadastro do usuário
     * @return mensagem de confirmação do cadastro
     */
    @PostMapping("/cadastro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));
    }
}
