package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "fotos_pet")
public class FotoPet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500) // URL da imagem
    private String url;

    @Column(nullable = false)
    private boolean principal; // Indica se é a foto principal para exibição

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonBackReference // Lado "filho" do relacionamento com Pet
    private Pet pet;

    // --- Construtores ---
    public FotoPet() {}

    public FotoPet(String url, boolean principal, Pet pet) {
        this.url = url;
        this.principal = principal;
        this.pet = pet;
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    // --- Sobrescrita de equals() e hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FotoPet fotoPet = (FotoPet) o;
        return id != null && Objects.equals(id, fotoPet.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0;
    }

    // --- Sobrescrita de toString() ---
    @Override
    public String toString() {
        return "FotoPet{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", principal=" + principal +
                '}';
    }
}