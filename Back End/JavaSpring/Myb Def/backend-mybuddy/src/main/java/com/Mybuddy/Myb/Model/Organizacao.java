package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "organizacoes")
public class Organizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
    private String emailContato;

    @Column(nullable = false, unique = true)
    private String cnpj;

    private String telefoneContato; // Pode ser nullable dependendo da sua regra

    @Column(nullable = false)
    private String endereco;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String website; // Pode ser nullable

    @OneToMany(mappedBy = "organizacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // Lado "pai" do relacionamento com Pet
    private Set<Pet> pets = new HashSet<>();

    @OneToMany(mappedBy = "organizacao", cascade = CascadeType.MERGE, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference // Lado "pai" do relacionamento com Usuario
    private Set<Usuario> usuarios = new HashSet<>();

    // --- Construtores ---
    public Organizacao() {
    }

    public Organizacao(String nomeFantasia, String emailContato, String cnpj, String telefoneContato, String endereco, String descricao, String website) {
        this.nomeFantasia = nomeFantasia;
        this.emailContato = emailContato;
        this.cnpj = cnpj;
        this.telefoneContato = telefoneContato;
        this.endereco = endereco;
        this.descricao = descricao;
        this.website = website;
    }

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Set<Pet> getPets() {
        return pets;
    }

    public void setPets(Set<Pet> pets) {
        this.pets = pets;
    }

    public Set<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    // --- Métodos Utilitários para gerenciar os relacionamentos bidirecionais ---
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

    // --- Sobrescrita de equals() e hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organizacao organizacao = (Organizacao) o;
        // Importante: use o ID para equals/hashCode se for uma entidade persistida
        return id != null && Objects.equals(id, organizacao.id);
    }

    @Override
    public int hashCode() {
        // Importante: use o ID para equals/hashCode se for uma entidade persistida
        return id != null ? Objects.hash(id) : 0;
    }

    // --- Sobrescrita de toString() ---
    @Override
    public String toString() {
        return "Organizacao{" +
                "id=" + id +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", emailContato='" + emailContato + '\'' +
                ", endereco='" + endereco + '\'' +
                '}';
    }
}