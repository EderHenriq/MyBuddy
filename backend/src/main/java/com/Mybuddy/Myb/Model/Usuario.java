package com.Mybuddy.Myb.Model;

import com.Mybuddy.Myb.Security.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

/**
 * Entidade Usuario adaptada para o MongoDB.
 * Representa os usuários (adotantes, administradores, ONGs e Petshops) da plataforma.
 */
@Document(collection = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario implements Identifiable {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;

    @Indexed(unique = true, sparse = true)
    private String email;

    private String telefone;

    private String password;

    @DocumentReference(lazy = true)
    @JsonIgnoreProperties({"usuarios", "pets"})
    @ToString.Exclude
    private Organizacao organizacao;

    @Indexed
    private Long petshopId;

    private Set<Long> petsAdotadosIds = new HashSet<>();

    @Indexed
    private String keycloakId;

    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    private String urlAvatar;

    @CreatedDate
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    private LocalDateTime dataAtualizacao;

    public Usuario(String nome, String email, String telefone, String password) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.password = password;
    }

    public Usuario(String nome, String email, String telefone, String password,
                   Organizacao organizacao, Set<Role> roles) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.password = password;
        this.organizacao = organizacao;
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>();
    }

    public void setRoles(Set<Role> roles) {
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>();
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }
}
