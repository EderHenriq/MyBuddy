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

    /**
     * Status de aprovação da ONG na plataforma.
     * Apenas ONGs APROVADAS podem criar campanhas de doação e listar pets publicamente.
     * O CNPJ é coletado no cadastro e validado pelo administrador antes da aprovação.
     */
    @Builder.Default
    private StatusAprovacao statusAprovacao = StatusAprovacao.PENDENTE_APROVACAO;

    /** Verifica se a ONG está aprovada para operar. */
    public boolean isAprovada() {
        return StatusAprovacao.APROVADO == this.statusAprovacao;
    }

    /** Verifica se a ONG ainda aguarda aprovação. */
    public boolean isPendente() {
        return StatusAprovacao.PENDENTE_APROVACAO == this.statusAprovacao;
    }

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