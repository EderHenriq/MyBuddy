package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade Cupom — representa cupons de desconto vinculados a um Petshop ou globais.
 * Campos adicionados (regras de negócio):
 *  - dataInicio / dataExpiracao  → validade temporal
 *  - valorMinimoPedido           → valor de pedido mínimo para aplicar o cupom
 *  - limiteUsoGeral              → quantas vezes o cupom pode ser usado no total (null = ilimitado)
 *  - usoAtual                    → contador de usos já realizados
 */
@Entity
@Table(name = "cupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "percentual_desconto", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualDesconto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petshop_id")
    @ToString.Exclude
    private Petshop petshop;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    /** Data a partir da qual o cupom é válido (inclusive). Null = sem restrição de início. */
    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    /** Data até a qual o cupom é válido (inclusive). Null = sem expiração. */
    @Column(name = "data_expiracao")
    private LocalDate dataExpiracao;

    /** Valor mínimo do pedido para o cupom ser aplicável. Null = sem valor mínimo. */
    @Column(name = "valor_minimo_pedido", precision = 10, scale = 2)
    private BigDecimal valorMinimoPedido;

    /** Limite total de usos do cupom na plataforma. Null = ilimitado. */
    @Column(name = "limite_uso_geral")
    private Integer limiteUsoGeral;

    /** Quantidade de vezes que este cupom já foi utilizado. */
    @Column(name = "uso_atual", nullable = false)
    @Builder.Default
    private int usoAtual = 0;

    /**
     * Verifica se o cupom está dentro do período de validade.
     */
    public boolean estaNoPeríodoVálido() {
        LocalDate hoje = LocalDate.now();
        if (dataInicio != null && hoje.isBefore(dataInicio)) {
            return false;
        }
        if (dataExpiracao != null && hoje.isAfter(dataExpiracao)) {
            return false;
        }
        return true;
    }

    /**
     * Verifica se o cupom ainda possui capacidade de uso geral disponível.
     */
    public boolean possuiUsoDisponivel() {
        return limiteUsoGeral == null || usoAtual < limiteUsoGeral;
    }

    /**
     * Incrementa atomicamente o contador de uso. Deve ser chamado dentro de @Transactional.
     */
    public void incrementarUso() {
        this.usoAtual++;
    }
}
