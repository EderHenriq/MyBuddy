package com.Mybuddy.Myb.Model;

import com.Mybuddy.Myb.Security.Role;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects; // Para equals e hashCode
import java.util.Set;

@Entity
@Table(name = "users") // Sugestão: "users" em minúsculo é uma convenção mais comum para tabelas
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Use Long (classe) para o ID em vez de long (primitivo) para consistência com JPA

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100) // E-mail é crucial, deve ser not null e ter um length
    private String email;

    @Column(length = 20) // Telefone com tamanho razoável
    private String telefone;

    @Column(nullable = false) // Uma senha é essencial para um usuário
    private String password; // Adicione este campo para a senha (hash da senha)

    // NOVO: Relacionamento ManyToOne com Organizacao
    // Um Usuário pode pertencer a UMA Organização (se for funcionário/admin de ONG).
    // @ManyToOne indica que esta é a parte "muitos" do relacionamento.
    // fetch = FetchType.LAZY: Carregamento otimizado. A organização só será carregada quando for acessada.
    // @JoinColumn(name = "organizacao_id"): Define a chave estrangeira na tabela 'users'.
    // nullable = true: Um usuário PODE existir sem uma organização (ex: adotante, admin do sistema).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id", nullable = true)
    private Organizacao organizacao;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    // --- Construtores ---
    public Usuario() {
    }

    // Construtor para criação básica de usuário (sem organização ou roles)
    public Usuario(String nome, String email, String telefone, String password) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.password = password; // Senha é importante
    }

    // Construtor completo, incluindo organização e roles
    public Usuario(String nome, String email, String telefone, String password, Organizacao organizacao, Set<Role> roles) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.password = password;
        this.organizacao = organizacao;
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>(); // Copia o set para evitar referências diretas
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // NOVO: Getter e Setter para Organizacao
    public Organizacao getOrganizacao() { return organizacao; }
    public void setOrganizacao(Organizacao organizacao) { this.organizacao = organizacao; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>(); }

    // --- Métodos Utilitários para gerenciar o relacionamento com Roles ---
    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    // --- Sobrescrita de equals() e hashCode() ---
    // Essencial para o bom funcionamento de coleções e comparações de entidades.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id != null && Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0;
    }

    // --- Sobrescrita de toString() ---
    // Útil para debugging.
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", organizacaoId=" + (organizacao != null ? organizacao.getId() : "N/A") +
                '}';
    }
}