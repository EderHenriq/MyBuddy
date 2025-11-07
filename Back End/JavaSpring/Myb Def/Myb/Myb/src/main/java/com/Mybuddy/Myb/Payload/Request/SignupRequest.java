package com.Mybuddy.Myb.Payload.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern; // Import para o Pattern
import jakarta.validation.constraints.Size;

import java.util.Set;

public class SignupRequest {

    // Campos do Usuário
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Size(max = 100, message = "O email não pode ter mais de 100 caracteres")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Formato de telefone inválido. Ex: (XX) XXXX-XXXX ou (XX) XXXXX-XXXX")
    private String telefone;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 40, message = "A senha deve ter entre 6 e 40 caracteres")
    private String password;

    // A lista de roles que o usuário terá (ex: ["adotante"], ["ong"])
    private Set<String> roles; // Renomeado de 'role' para 'roles' para consistência

    // --- Campos específicos para CRIAR uma nova ONG (se a role for "ong") ---
    // Estes campos devem vir do frontend apenas se o usuário for do tipo ONG
    private String organizacaoCnpj;
    private String organizacaoNomeFantasia;
    private String organizacaoEmailContato;
    private String organizacaoTelefoneContato;
    private String organizacaoEndereco;
    private String organizacaoDescricao; // Opcional
    private String organizacaoWebsite;   // Opcional

    // --- Getters e Setters ---
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    // Getters e Setters para os campos da ONG
    public String getOrganizacaoCnpj() { return organizacaoCnpj; }
    public void setOrganizacaoCnpj(String organizacaoCnpj) { this.organizacaoCnpj = organizacaoCnpj; }

    public String getOrganizacaoNomeFantasia() { return organizacaoNomeFantasia; }
    public void setOrganizacaoNomeFantasia(String organizacaoNomeFantasia) { this.organizacaoNomeFantasia = organizacaoNomeFantasia; }

    public String getOrganizacaoEmailContato() { return organizacaoEmailContato; }
    public void setOrganizacaoEmailContato(String organizacaoEmailContato) { this.organizacaoEmailContato = organizacaoEmailContato; }

    public String getOrganizacaoTelefoneContato() { return organizacaoTelefoneContato; }
    public void setOrganizacaoTelefoneContato(String organizacaoTelefoneContato) { this.organizacaoTelefoneContato = organizacaoTelefoneContato; }

    public String getOrganizacaoEndereco() { return organizacaoEndereco; }
    public void setOrganizacaoEndereco(String organizacaoEndereco) { this.organizacaoEndereco = organizacaoEndereco; }

    public String getOrganizacaoDescricao() { return organizacaoDescricao; }
    public void setOrganizacaoDescricao(String organizacaoDescricao) { this.organizacaoDescricao = organizacaoDescricao; }

    public String getOrganizacaoWebsite() { return organizacaoWebsite; }
    public void setOrganizacaoWebsite(String organizacaoWebsite) { this.organizacaoWebsite = organizacaoWebsite; }
}