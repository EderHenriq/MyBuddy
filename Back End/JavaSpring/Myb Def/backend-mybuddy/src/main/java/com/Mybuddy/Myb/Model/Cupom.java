package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

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
}
