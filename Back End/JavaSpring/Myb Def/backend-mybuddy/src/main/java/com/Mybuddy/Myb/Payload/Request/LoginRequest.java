package com.Mybuddy.Myb.Payload.Request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    private String email; // Campo para o e-mail do usuário

    @NotBlank // NOVO: Este campo é para a senha real do usuário
    private String password; // Campo para armazenar a senha do usuário

    // --- Getters e Setters ---
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() { // Getter para a senha
        return password;
    }

    public void setPassword(String password) { // Setter para a senha
        this.password = password;
    }
}