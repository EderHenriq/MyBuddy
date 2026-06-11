package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Payment;
import com.Mybuddy.Myb.Model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Payment (PostgreSQL).
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByMpPreferenceId(String mpPreferenceId);
    
    Optional<Payment> findByMpPaymentId(String mpPaymentId);

    List<Payment> findByUsuarioId(Long usuarioId);

    List<Payment> findByPetId(Long petId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPedidoId(Long pedidoId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    java.math.BigDecimal sumAmountByStatus(PaymentStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT p.usuarioId) FROM Payment p WHERE p.status = :status")
    long countDistinctUsuarioIdByStatus(PaymentStatus status);
}
