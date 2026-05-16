package com.Mybuddy.Myb.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Mybuddy.Myb.Model.Payment;
import com.Mybuddy.Myb.Model.PaymentStatus;
import com.Mybuddy.Myb.Repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public Optional<Payment> findByMpPreferenceId(String mpPreferenceId) {
        return paymentRepository.findByMpPreferenceId(mpPreferenceId);
    }

    public Optional<Payment> findByMpPaymentId(String mpPaymentId) {
        return paymentRepository.findByMpPaymentId(mpPaymentId);
    }

    public List<Payment> findByUsuarioId(Long usuarioId) {
        return paymentRepository.findByUsuarioId(usuarioId);
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @Transactional
    public Payment updateStatus(Payment payment, PaymentStatus newStatus) {
        log.info("Atualizando status do pagamento ID {}: de {} para -> {}", payment.getId(), payment.getStatus(), newStatus);
        payment.setStatus(newStatus);
        return paymentRepository.save(payment);
    }
}