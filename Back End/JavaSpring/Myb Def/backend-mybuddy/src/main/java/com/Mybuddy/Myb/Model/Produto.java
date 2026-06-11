package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 150, nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategoria_id", nullable = false)
    private SubCategoria subCategoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer estoque;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatusProduto status = StatusProduto.ATIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petshop_id", nullable = false)
    @JsonBackReference(value = "petshop-produtos")
    @ToString.Exclude
    private Petshop petshop;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "produto-fotos")
    @ToString.Exclude
    @org.hibernate.annotations.BatchSize(size = 20)
    private Set<FotoProduto> fotos = new HashSet<>();

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "produto-avaliacoes")
    @ToString.Exclude
    @org.hibernate.annotations.BatchSize(size = 20)
    private Set<AvaliacaoProduto> avaliacoes = new HashSet<>();

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    public void addFoto(FotoProduto foto) {
        if (foto != null) {
            this.fotos.add(foto);
            foto.setProduto(this);
        }
    }

    public void removeFoto(FotoProduto foto) {
        if (foto != null) {
            this.fotos.remove(foto);
            foto.setProduto(null);
        }
    }
}
