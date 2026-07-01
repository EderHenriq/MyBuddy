package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
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

    @Column(name = "valor_minimo_frete_gratis", precision = 10, scale = 2)
    private BigDecimal valorMinimoFreteGratis;

    private Double latitude;
    private Double longitude;

    @Column(name = "raio_entrega_km")
    private Double raioEntregaKm;

    /** Percentual de comissão da plataforma sobre as vendas do petshop. Padrão: 10%. */
    @Column(name = "taxa_comissao", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxaComissao = new BigDecimal("10.00");

    @Column(name = "mp_user_id", length = 100)
    private String mpUserId;

    @Column(name = "mp_merchant_account_id", length = 100)
    private String mpMerchantAccountId;

    /**
     * Status de aprovação do Petshop na plataforma.
     * Apenas Petshops APROVADOS podem cadastrar produtos e aparecer publicamente.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status_aprovacao", nullable = false, length = 30)
    @Builder.Default
    private StatusAprovacao statusAprovacao = StatusAprovacao.PENDENTE_APROVACAO;

    /** Verifica se o petshop está aprovado para operar. */
    public boolean isAprovado() {
        return StatusAprovacao.APROVADO == this.statusAprovacao;
    }

    /** Verifica se o petshop ainda aguarda aprovação. */
    public boolean isPendente() {
        return StatusAprovacao.PENDENTE_APROVACAO == this.statusAprovacao;
    }

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
