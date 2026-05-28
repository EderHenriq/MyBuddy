package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Payment;
import com.Mybuddy.Myb.Model.PaymentStatus;
import com.Mybuddy.Myb.Repository.jpa.PaymentRepository;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("50.00"));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setMpPreferenceId("pref-123");
        payment.setMpPaymentId("pay-456");
    }

    // ===================== SAVE =====================

    @Test
    void deveSalvarPaymentComSucesso() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.save(payment);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(paymentRepository, times(1)).save(payment);
    }

    // ===================== FIND BY ID =====================

    @Test
    void deveBuscarPaymentPorIdExistente() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void deveRetornarVazioAoBuscarPaymentPorIdInexistente() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ===================== FIND BY PREFERENCE ID =====================

    @Test
    void deveBuscarPaymentPorMpPreferenceId() {
        when(paymentRepository.findByMpPreferenceId("pref-123")).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.findByMpPreferenceId("pref-123");

        assertThat(result).isPresent();
        assertThat(result.get().getMpPreferenceId()).isEqualTo("pref-123");
    }

    @Test
    void deveRetornarVazioAoBuscarPaymentPorPreferenceIdInexistente() {
        when(paymentRepository.findByMpPreferenceId("inexistente")).thenReturn(Optional.empty());

        Optional<Payment> result = paymentService.findByMpPreferenceId("inexistente");

        assertThat(result).isEmpty();
    }

    // ===================== FIND BY PAYMENT ID =====================

    @Test
    void deveBuscarPaymentPorMpPaymentId() {
        when(paymentRepository.findByMpPaymentId("pay-456")).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.findByMpPaymentId("pay-456");

        assertThat(result).isPresent();
        assertThat(result.get().getMpPaymentId()).isEqualTo("pay-456");
    }

    @Test
    void deveRetornarVazioAoBuscarPaymentPorMpPaymentIdInexistente() {
        when(paymentRepository.findByMpPaymentId("inexistente")).thenReturn(Optional.empty());

        Optional<Payment> result = paymentService.findByMpPaymentId("inexistente");

        assertThat(result).isEmpty();
    }

    // ===================== FIND BY USUARIO ID =====================

    @Test
    void deveBuscarPaymentsPorUsuarioId() {
        when(paymentRepository.findByUsuarioId(1L)).thenReturn(List.of(payment));

        List<Payment> result = paymentService.findByUsuarioId(1L);

        assertThat(result).hasSize(1);
        verify(paymentRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoTemPayments() {
        when(paymentRepository.findByUsuarioId(99L)).thenReturn(List.of());

        List<Payment> result = paymentService.findByUsuarioId(99L);

        assertThat(result).isEmpty();
    }

    // ===================== FIND BY STATUS =====================

    @Test
    void deveBuscarPaymentsPorStatus() {
        when(paymentRepository.findByStatus(PaymentStatus.PENDING)).thenReturn(List.of(payment));

        List<Payment> result = paymentService.findByStatus(PaymentStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverPaymentsComStatus() {
        when(paymentRepository.findByStatus(PaymentStatus.APPROVED)).thenReturn(List.of());

        List<Payment> result = paymentService.findByStatus(PaymentStatus.APPROVED);

        assertThat(result).isEmpty();
    }

    // ===================== UPDATE STATUS =====================

    @Test
    void deveAtualizarStatusDoPaymentComSucesso() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.updateStatus(payment, PaymentStatus.APPROVED);

        assertThat(result).isNotNull();
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void deveAtualizarStatusParaCancelled() {
        payment.setStatus(PaymentStatus.CANCELLED);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.updateStatus(payment, PaymentStatus.CANCELLED);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    void deveAtualizarStatusParaRefunded() {
        payment.setStatus(PaymentStatus.REFUNDED);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.updateStatus(payment, PaymentStatus.REFUNDED);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }
}