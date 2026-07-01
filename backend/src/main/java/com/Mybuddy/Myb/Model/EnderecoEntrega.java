package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade que representa o endereço de entrega associado a um pedido do marketplace.
 */
@Entity
@Table(name = "enderecos_entrega")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EnderecoEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 20)
    private String cep;

    @Column(nullable = false, length = 255)
    private String logradouro;

    @Column(nullable = false, length = 50)
    private String numero;

    @Column(length = 255)
    private String complemento;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 50)
    private String estado;

    private Double latitude;
    private Double longitude;
}
