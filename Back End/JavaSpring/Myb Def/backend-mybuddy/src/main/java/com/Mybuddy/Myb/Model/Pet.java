package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 80, nullable = false)
    private String nome;

    @Column(length = 60, nullable = false)
    private String raca;

    @Column(nullable = false)
    private Integer idade;

    @Enumerated(EnumType.STRING)
    @Column(length = 40, nullable = false)
    private Especie especie;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Porte porte;

    @Column(length = 30, nullable = false)
    private String cor;

    @Column(length = 60)
    private String pelagem;

    @Column(length = 10, nullable = false)
    private String sexo;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private Set<FotoPet> fotos = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatusAdocao statusAdocao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private Organizacao organizacao;

    @Column(nullable = false)
    private boolean microchipado;

    @Column(nullable = false)
    private boolean vacinado;

    @Column(nullable = false)
    private boolean castrado;

    @Column(length = 100)
    private String cidade;

    @Column(length = 100)
    private String estado;

    public Pet(String nome, String raca, Integer idade, Especie especie, Porte porte,
               String cor, String pelagem, String sexo, Organizacao organizacao,
               boolean microchipado, boolean vacinado, boolean castrado,
               String cidade, String estado) {
        this.nome = nome;
        this.raca = raca;
        this.idade = idade;
        this.especie = especie;
        this.porte = porte;
        this.cor = cor;
        this.pelagem = pelagem;
        this.sexo = sexo;
        this.organizacao = organizacao;
        this.microchipado = microchipado;
        this.vacinado = vacinado;
        this.castrado = castrado;
        this.cidade = cidade;
        this.estado = estado;
        this.statusAdocao = StatusAdocao.DISPONIVEL;
    }

    public void addFoto(FotoPet foto) {
        if (foto != null && !this.fotos.contains(foto)) {
            this.fotos.add(foto);
            foto.setPet(this);
        }
    }

    public void removeFoto(FotoPet foto) {
        if (foto != null && this.fotos.contains(foto)) {
            this.fotos.remove(foto);
            foto.setPet(null);
        }
    }

    public void clearFotos() {
        this.fotos.forEach(foto -> foto.setPet(null));
        this.fotos.clear();
    }
}