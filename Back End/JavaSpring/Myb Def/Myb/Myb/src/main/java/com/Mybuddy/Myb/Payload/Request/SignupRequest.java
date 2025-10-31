package com.Mybuddy.Myb.Payload.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 100) // Nome mais flexível, para nomes completos
    private String nome;

    @NotBlank
    @Size(max = 100) // Email com tamanho máximo mais comum
    @Email
    private String email;

    @NotBlank // Telefone agora é um campo real do usuário
    @Size(min = 10, max = 20) // Tamanho realista para números de telefone
    private String telefone;

    @NotBlank // NOVO: Campo para a senha real do usuário
    @Size(min = 6, max = 40) // Tamanho da senha, de 6 a 40 caracteres (deve ser o hash no banco)
    private String password;

    private Set<String> role;

    // NOVO: Campos para associar o usuário a uma organização (especialmente para ROLE_ONG)
    private Long organizacaoId;    // ID de uma organização existente
    private String organizacaoCnpj; // CNPJ de uma organização existente (alternativa ao ID)

    // --- Getters e Setters ---
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getPassword() { return password; } // Getter para a senha
    public void setPassword(String password) { this.password = password; } // Setter para a senha

    public Set<String> getRole() { return role; }
    public void setRole(Set<String> role) { this.role = role; }

    public Long getOrganizacaoId() { return organizacaoId; }
    public void setOrganizacaoId(Long organizacaoId) { this.organizacaoId = organizacaoId; }

    public String getOrganizacaoCnpj() { return organizacaoCnpj; }
    public void setOrganizacaoCnpj(String organizacaoCnpj) { this.organizacaoCnpj = organizacaoCnpj; }
}