package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mp_preapproval_id", unique = true)
    private String mpPreapprovalId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "organizacao_id")
    private Long organizacaoId;

    private BigDecimal amount;
    private String frequency; // monthly, weekly
    private String status; // pending, authorized, cancelled

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = "pending";
    }
}
