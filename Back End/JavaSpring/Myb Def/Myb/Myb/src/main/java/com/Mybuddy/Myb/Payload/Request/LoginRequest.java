package com.Mybuddy.Myb.Payload.Request; // Declara o pacote onde esta classe de requisição está localizada.
// O termo "Payload.Request" geralmente indica que a classe é um DTO (Data Transfer Object)
// usado para receber dados de uma requisição HTTP.

import jakarta.validation.constraints.NotBlank; // Importa a anotação de validação @NotBlank, que garante que uma string não seja nula, vazia ou contenha apenas espaços em branco.

// Declara a classe LoginRequest.
// Esta classe é um DTO que representa os dados esperados no corpo de uma requisição HTTP
// para autenticação (login) de um usuário.
public class LoginRequest {
    @NotBlank // Anotação de validação que garante que o campo 'email' não seja nulo, vazio ou composto apenas por espaços em branco.
    private String email; // Campo para armazenar o e-mail do usuário (renomeado de 'username' para 'email', indicando que o login é feito por e-mail).

    @NotBlank // Anotação de validação que garante que o campo 'telefone' não seja nulo, vazio ou composto apenas por espaços em branco.
    private String telefone; // Campo para armazenar o telefone do usuário (renomeado de 'password' para 'telefone', o que é uma escolha incomum para senha, mas seguindo o código).

    // Métodos Getters e Setters para cada atributo da classe, permitindo acesso e modificação dos valores dos campos.

    public String getEmail() { // Método getter para o campo 'email'.
        return email;
    }

    public void setEmail(String email) { // Método setter para o campo 'email'.
        this.email = email;
    }

    public String getTelefone() { // Método getter para o campo 'telefone'.
        return telefone;
    }

    public void setTelefone(String telefone) { // Método setter para o campo 'telefone'.
        this.telefone = telefone;
    }
}