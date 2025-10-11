package com.Mybuddy.Myb.Payload.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set; // Importe Set para coleções de roles

public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String nome; // ou username, dependendo do que você usa como identificador

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 15) // Assumindo que telefone tem um tamanho razoável
    private String telefone; // Sua "senha"

    // <<--- NOVO CAMPO: Lista de Roles
    private Set<String> role; // Vai receber "adotante" ou "ong"

    // Getters e Setters para 'nome', 'email', 'telefone' já devem existir

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Set<String> getRole() { // Getter para a role
        return role;
    }

    public void setRole(Set<String> role) { // Setter para a role
        this.role = role;
    }
}