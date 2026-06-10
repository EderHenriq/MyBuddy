package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidade Organizacao (ONG) adaptada para o MongoDB.
 * Representa as ONGs de resgate e adoção de animais.
 */
@Document(collection = "organizacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Organizacao implements Identifiable {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String nomeFantasia;

    private String emailContato;

    private String cnpj;

    private String telefoneContato;

    private String endereco;

    private String descricao;

    private String website;

    @DocumentReference(lazy = true)
    @JsonManagedReference
    @ToString.Exclude
    @Builder.Default
    private Set<Pet> pets = new HashSet<>();

    @DocumentReference(lazy = true)
    @JsonManagedReference
    @ToString.Exclude
    @Builder.Default
    private Set<Usuario> usuarios = new HashSet<>();

    // Métodos de negócio para gerenciar os relacionamentos bidirecionais
    public void addPet(Pet pet) {
        this.pets.add(pet);
        if (pet != null) {
            pet.setOrganizacao(this);
        }
    }

    public void removePet(Pet pet) {
        this.pets.remove(pet);
        if (pet != null) {
            pet.setOrganizacao(null);
        }
    }

    public void addUsuario(Usuario usuario) {
        this.usuarios.add(usuario);
        if (usuario != null) {
            usuario.setOrganizacao(this);
        }
    }

    public void removeUsuario(Usuario usuario) {
        this.usuarios.remove(usuario);
        if (usuario != null) {
            usuario.setOrganizacao(null);
        }
    }
}