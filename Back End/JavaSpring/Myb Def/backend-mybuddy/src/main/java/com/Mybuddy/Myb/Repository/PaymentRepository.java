package com.Mybuddy.Myb.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Mybuddy.Myb.Model.Payment;
import com.Mybuddy.Myb.Model.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByMpPreferenceId(String mpPreferenceId);
    
    Optional<Payment> findByMpPaymentId(String mpPaymentId);

    List<Payment> findByUsuarioId(Long usuarioId);

    List<Payment> findByPetId(Long petId);

    List<Payment> findByStatus(PaymentStatus status);
}
