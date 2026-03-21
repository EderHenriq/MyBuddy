package com.Mybuddy.Myb.Model;

import com.Mybuddy.Myb.Security.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id")
    @JsonBackReference
    @ToString.Exclude
    private Organizacao organizacao;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    public Usuario(String nome, String email, String telefone, String password) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.password = password;
    }

    // Construtor completo usado pelo DataInitializer
    public Usuario(String nome, String email, String telefone, String password,
                   Organizacao organizacao, Set<Role> roles) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.password = password;
        this.organizacao = organizacao;
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>();
    }

    // Setter customizado mantido — garante cópia defensiva do Set
    public void setRoles(Set<Role> roles) {
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>();
    }

    // Métodos de negócio — mantidos pois têm lógica própria
    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }
}