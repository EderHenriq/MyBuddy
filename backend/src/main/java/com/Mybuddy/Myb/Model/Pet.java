package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;


@Document(collection = "pets")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pet implements Identifiable {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;

    private String raca;

    private Integer idade;

    private Especie especie;

    private Porte porte;

    private String cor;

    private String pelagem;

    private String sexo;

    @ToString.Exclude
    private Set<FotoPet> fotos = new HashSet<>();

    private StatusAdocao statusAdocao = StatusAdocao.DISPONIVEL;

    @DocumentReference(lazy = true)
    @JsonBackReference
    @ToString.Exclude
    private Organizacao organizacao;

    private boolean microchipado;

    private boolean vacinado;

    private boolean castrado;

    private String cidade;

    private String estado;

    private Long adotanteId;

    private Double peso;

    private String descricao;

    @CreatedDate
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    private LocalDateTime dataAtualizacao;

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
        }
    }

    public void removeFoto(FotoPet foto) {
        if (foto != null && this.fotos.contains(foto)) {
            this.fotos.remove(foto);
        }
    }

    public void clearFotos() {
        this.fotos.clear();
    }
}