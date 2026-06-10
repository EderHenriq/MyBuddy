package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade Petshop para o JPA/PostgreSQL.
 * Representa os petshops parceiros da plataforma.
 */
@Entity
@Table(name = "petshops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Petshop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nome_fantasia", nullable = false)
    private String nomeFantasia;

    @Column(name = "email_contato")
    private String emailContato;

    @Column(unique = true, nullable = false)
    private String cnpj;

    @Column(name = "telefone_contato")
    private String telefoneContato;

    private String endereco;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String website;

    @OneToMany(mappedBy = "petshop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "petshop-produtos")
    @ToString.Exclude
    @Builder.Default
    private Set<Produto> produtos = new HashSet<>();

    @OneToMany(mappedBy = "petshop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "petshop-pedidos")
    @ToString.Exclude
    @Builder.Default
    private Set<Pedido> pedidos = new HashSet<>();
}
