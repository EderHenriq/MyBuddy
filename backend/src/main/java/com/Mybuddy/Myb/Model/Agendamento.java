package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa o agendamento de um serviço de um petshop por um cliente.
 */
@Entity
@Table(name = "agendamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    @Column(name = "profissional_nome")
    private String profissionalNome;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}
