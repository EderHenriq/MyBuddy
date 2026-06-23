package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Payment;
import com.Mybuddy.Myb.Model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Payment (PostgreSQL).
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByMpPreferenceId(String mpPreferenceId);
    
    Optional<Payment> findByMpPaymentId(String mpPaymentId);

    List<Payment> findByUsuarioId(Long usuarioId);

    List<Payment> findByPetId(Long petId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPedidoId(Long pedidoId);

    List<Payment> findByOrganizacaoId(Long organizacaoId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query("UPDATE Payment p SET p.petId = null WHERE p.petId = :petId")
    void nullifyPetId(@org.springframework.data.repository.query.Param("petId") Long petId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query("UPDATE Payment p SET p.organizacaoId = null WHERE p.organizacaoId = :organizacaoId")
    void nullifyOrganizacaoId(@org.springframework.data.repository.query.Param("organizacaoId") Long organizacaoId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    java.math.BigDecimal sumAmountByStatus(PaymentStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT p.usuarioId) FROM Payment p WHERE p.status = :status")
    long countDistinctUsuarioIdByStatus(PaymentStatus status);
}
