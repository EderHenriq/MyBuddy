package com.Mybuddy.Myb.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Mybuddy.Myb.DTO.PaymentCreationResult;
import com.Mybuddy.Myb.DTO.PaymentRequestDTO;
import com.Mybuddy.Myb.Model.Payment;
import com.Mybuddy.Myb.Model.PaymentStatus;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Model.CampanhaDoacao;
import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.StatusPedido;
import com.Mybuddy.Myb.Repository.jpa.PaymentRepository;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.mongo.CampanhaDoacaoRepository;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
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
@SuppressWarnings("null")
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final PetRepository petRepository;
    private final CampanhaDoacaoRepository campanhaDoacaoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional
    public PaymentCreationResult createPayment(Usuario usuario, PaymentRequestDTO request)
            throws MPException, MPApiException {

        var pet = request.petId() != null
                ? petRepository.findById(request.petId())
                        .orElseThrow(() -> new RuntimeException("Pet não encontrado: " + request.petId()))
                : null;

        if (request.campanhaId() != null) {
            CampanhaDoacao campanha = campanhaDoacaoRepository.findById(request.campanhaId())
                    .orElseThrow(() -> new RuntimeException("Campanha de doação não encontrada: " + request.campanhaId()));
            if (!"ATIVA".equals(campanha.getStatus())) {
                throw new IllegalStateException("A campanha de doação selecionada não está ativa.");
            }
        }

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
                .pending("http://localhost/checkout/pendente")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(item))
                .backUrls(backUrls)
                .autoReturn("approved")
                .build();
            
        Preference preference = client.create(preferenceRequest);

        Payment payment = new Payment();
        payment.setUsuarioId(usuario.getId());
        payment.setPetId(pet != null ? pet.getId() : null);
        payment.setCampanhaId(request.campanhaId());
        payment.setOrganizacaoId(request.organizacaoId());
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
        PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(newStatus);
        Payment saved = paymentRepository.save(payment);

        if (newStatus == PaymentStatus.APPROVED && oldStatus != PaymentStatus.APPROVED) {
            if (payment.getCampanhaId() != null) {
                campanhaDoacaoRepository.findById(payment.getCampanhaId()).ifPresent(campanha -> {
                    java.math.BigDecimal arrecadado = campanha.getArrecadado() != null ? campanha.getArrecadado() : java.math.BigDecimal.ZERO;
                    campanha.setArrecadado(arrecadado.add(payment.getAmount()));
                    if (campanha.getMeta() != null && campanha.getArrecadado().compareTo(campanha.getMeta()) >= 0) {
                        campanha.setStatus("META_ATINGIDA");
                    }
                    campanhaDoacaoRepository.save(campanha);
                });
            }
            if (payment.getPedido() != null) {
                Pedido pedido = payment.getPedido();
                pedido.setStatus(StatusPedido.PAGO);
                pedidoRepository.save(pedido);
            }
        } else if ((newStatus == PaymentStatus.REJECTED || newStatus == PaymentStatus.CANCELLED)
                && oldStatus != PaymentStatus.REJECTED && oldStatus != PaymentStatus.CANCELLED) {
            if (payment.getPedido() != null) {
                Pedido pedido = payment.getPedido();
                pedido.setStatus(StatusPedido.CANCELADO);
                pedidoRepository.save(pedido);
            }
        }
        return saved;
    }
}