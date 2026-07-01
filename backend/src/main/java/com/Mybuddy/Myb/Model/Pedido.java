package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petshop_id", nullable = false)
    @JsonBackReference(value = "petshop-pedidos")
    @ToString.Exclude
    private Petshop petshop;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_entrega_id")
    private EnderecoEntrega enderecoEntrega;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    @org.hibernate.annotations.BatchSize(size = 20)
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "valor_frete", precision = 10, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @Column(name = "cupom_desconto", length = 100)
    private String cupomDesconto;

    @Column(name = "valor_desconto", precision = 10, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatusPedido status = StatusPedido.PENDENTE;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    /**
     * Adiciona um item ao pedido, vinculando a referência bidirecional e somando
     * o subtotal do item ao valor total do pedido.
     *
     * @param item item a ser adicionado
     */
    public void addItem(ItemPedido item) {
        this.itens.add(item);
        item.setPedido(this);
        this.valorTotal = this.valorTotal.add(item.getSubtotal());
    }

    /**
     * Remove um item do pedido, desvinculando a referência bidirecional e subtraindo
     * o subtotal do item do valor total do pedido.
     *
     * @param item item a ser removido
     */
    public void removeItem(ItemPedido item) {
        this.itens.remove(item);
        item.setPedido(null);
        this.valorTotal = this.valorTotal.subtract(item.getSubtotal());
    }
}
