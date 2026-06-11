package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade CupomUsuario — registra o uso de um cupom por um usuário específico.
 * Garante que cada usuário utilize um cupom no máximo uma vez (uso único por usuário).
 *
 * A unicidade é forçada via constraint composta (cupom_id, usuario_id).
 */
@Entity
@Table(
    name = "cupons_usuarios",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cupom_usuario",
        columnNames = {"cupom_id", "usuario_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CupomUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /** Referência ao cupom utilizado. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cupom_id", nullable = false)
    @ToString.Exclude
    private Cupom cupom;

    /** ID do usuário (vindo do Keycloak/MongoDB — armazenado como Long para consistência). */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    /** ID do pedido no qual o cupom foi aplicado. */
    @Column(name = "pedido_id")
    private Long pedidoId;

    /** Data e hora do uso do cupom. */
    @Column(name = "usado_em", nullable = false)
    private LocalDateTime usadoEm;

    @PrePersist
    private void prePersist() {
        if (usadoEm == null) {
            usadoEm = LocalDateTime.now();
        }
    }
}
