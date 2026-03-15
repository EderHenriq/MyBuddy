package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "organizacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"id", "nomeFantasia", "cnpj", "emailContato", "endereco"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Organizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
    private String emailContato;

    @Column(nullable = false, unique = true)
    private String cnpj;

    private String telefoneContato;

    @Column(nullable = false)
    private String endereco;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String website;

    @OneToMany(mappedBy = "organizacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private Set<Pet> pets = new HashSet<>();

    @OneToMany(mappedBy = "organizacao", cascade = CascadeType.MERGE, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private Set<Usuario> usuarios = new HashSet<>();

    // Métodos de negócio — mantidos pois gerenciam relacionamentos bidirecionais
    public void addPet(Pet pet) {
        this.pets.add(pet);
        pet.setOrganizacao(this);
    }

    public void removePet(Pet pet) {
        this.pets.remove(pet);
        pet.setOrganizacao(null);
    }

    public void addUsuario(Usuario usuario) {
        this.usuarios.add(usuario);
        usuario.setOrganizacao(this);
    }

    public void removeUsuario(Usuario usuario) {
        this.usuarios.remove(usuario);
        usuario.setOrganizacao(null);
    }
}