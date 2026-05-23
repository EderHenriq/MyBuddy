package com.Mybuddy.Myb.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Mybuddy.Myb.DTO.PaymentCreationResult;
import com.Mybuddy.Myb.DTO.PaymentRequestDTO;
import com.Mybuddy.Myb.Model.Payment;
import com.Mybuddy.Myb.Model.PaymentStatus;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.PaymentRepository;
import com.Mybuddy.Myb.Repository.PetRepository;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final PetRepository petRepository;

    @Transactional
    public PaymentCreationResult createPayment(Usuario usuario, PaymentRequestDTO request)
            throws MPException, MPApiException {

        var pet = request.petId() != null
                ? petRepository.findById(request.petId())
                        .orElseThrow(() -> new RuntimeException("Pet não encontrado: " + request.petId()))
                : null;

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(request.description() != null ? request.description()
                        : pet != null ? "Adoção - " + pet.getNome()
                        : "Doação MyBuddy")
                .quantity(1)
                .unitPrice(request.amount())
                .build();

        PreferenceClient client = new PreferenceClient();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("http://localhost/checkout/confirmacao")
                .failure("http://localhost/checkout/confirmacao")
                .pending("http://localhost/checkout/confirmacao")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(item))
                .backUrls(backUrls)
                .autoReturn("approved")
                .build();
            
        Preference preference = client.create(preferenceRequest);

        Payment payment = new Payment();
        payment.setUsuario(usuario);
        payment.setPet(pet);
        payment.setAmount(request.amount());
        payment.setMpPreferenceId(preference.getId());

        Payment saved = paymentRepository.save(payment);

        log.info("Payment criado: id={}, preferenceId={}", saved.getId(), saved.getMpPreferenceId());

        String initPoint = preference.getSandboxInitPoint() != null
                ? preference.getSandboxInitPoint()
                : preference.getInitPoint();
        return new PaymentCreationResult(saved, initPoint);
    }

    public String getInitPoint(String preferenceId) throws MPException, MPApiException {
        PreferenceClient client = new PreferenceClient();
        Preference preference = client.get(preferenceId);
        return preference.getInitPoint();
    }

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