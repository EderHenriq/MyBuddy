package com.Mybuddy.Myb.Payload.Request; // Declara o pacote onde esta classe de requisição está localizada.
// O termo "Payload.Request" indica que é um DTO (Data Transfer Object)
// para receber dados de uma requisição HTTP.

import jakarta.validation.constraints.Email; // Importa a anotação @Email, que valida se uma string é um formato de e-mail válido.
import jakarta.validation.constraints.NotBlank; // Importa a anotação @NotBlank, que garante que uma string não seja nula, vazia ou contenha apenas espaços em branco.
import jakarta.validation.constraints.Size; // Importa a anotação @Size, que verifica o tamanho de uma string ou coleção.
import java.util.Set; // Importa a interface Set para lidar com coleções de elementos únicos (neste caso, as roles).

// Declara a classe SignupRequest.
// Esta classe é um DTO que representa os dados esperados no corpo de uma requisição HTTP
// para registrar um novo usuário no sistema.

public class SignupRequest {
    @NotBlank // Garante que o campo 'nome' não seja nulo, vazio ou contenha apenas espaços em branco.
    @Size(min = 3, max = 20) // Define que o campo 'nome' deve ter entre 3 e 20 caracteres.
    private String nome; // Campo para o nome do usuário (ou 'username', dependendo da convenção da aplicação).

    @NotBlank // Garante que o campo 'email' não seja nulo, vazio ou contenha apenas espaços em branco.
    @Size(max = 50) // Define que o campo 'email' deve ter no máximo 50 caracteres.
    @Email // Valida que o conteúdo do campo 'email' seja um endereço de e-mail válido.
    private String email; // Campo para o endereço de e-mail do usuário.

    @NotBlank // Garante que o campo 'telefone' não seja nulo, vazio ou contenha apenas espaços em branco.
    @Size(min = 6, max = 15) // Define que o campo 'telefone' deve ter entre 6 e 15 caracteres (assumindo um tamanho razoável para um número de telefone).
    private String telefone; // Campo para o número de telefone do usuário. No contexto deste código, está sendo usado no lugar de uma senha tradicional.

    // <<--- NOVO CAMPO: Lista de Roles
    private Set<String> role; // Campo que irá receber um conjunto de strings representando os papéis/perfis que o novo usuário terá.
    // Ex: "adotante" ou "ong". Este campo permite atribuir um ou mais papéis no momento do cadastro.

    // Getters e Setters para cada atributo da classe, permitindo acesso e modificação dos valores dos campos.

    public String getNome() { // Método getter para o campo 'nome'.
        return nome;
    }

    public void setNome(String nome) { // Método setter para o campo 'nome'.
        this.nome = nome;
    }

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

    public Set<String> getRole() { // Método getter para o campo 'role' (conjunto de papéis).
        return role;
    }

    public void setRole(Set<String> role) { // Método setter para o campo 'role' (conjunto de papéis).
        this.role = role;
    }
}