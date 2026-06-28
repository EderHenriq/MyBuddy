package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "campanhas_doacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CampanhaDoacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal meta;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal arrecadado;
    
    @Column(name = "pet_id")
    private Long petId; // Opcional - se a campanha for para um pet específico

    @Column(name = "organizacao_id", nullable = false)
    private Long organizacaoId; // Obrigatório - a ONG dona da campanha
    
    @Column(nullable = false, length = 50)
    private String categoria; // CIRURGIA, RACAO, TRATAMENTO, GERAL

    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;

    @Column(nullable = false, length = 30)
    private String status; // ATIVA, ENCERRADA, META_ATINGIDA

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}
