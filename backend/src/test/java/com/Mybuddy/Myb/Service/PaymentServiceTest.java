package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Payment;
import com.Mybuddy.Myb.Model.PaymentStatus;
import com.Mybuddy.Myb.Model.CampanhaDoacao;
import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.StatusPedido;
import com.Mybuddy.Myb.Repository.jpa.CampanhaDoacaoRepository;
import com.Mybuddy.Myb.Repository.jpa.PaymentRepository;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private CampanhaDoacaoRepository campanhaDoacaoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

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

    @Test
    void deveAtualizarStatusDoPaymentEAtualizarCampanhaSeAprovado() {
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCampanhaId(10L);
        payment.setAmount(new BigDecimal("100.00"));

        CampanhaDoacao campanha = new CampanhaDoacao();
        campanha.setId(10L);
        campanha.setArrecadado(new BigDecimal("50.00"));
        campanha.setMeta(new BigDecimal("120.00"));

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(campanhaDoacaoRepository.findById(10L)).thenReturn(Optional.of(campanha));
        when(campanhaDoacaoRepository.save(any(CampanhaDoacao.class))).thenReturn(campanha);

        Payment result = paymentService.updateStatus(payment, PaymentStatus.APPROVED);

        assertThat(result).isNotNull();
        assertThat(campanha.getArrecadado()).isEqualTo(new BigDecimal("150.00"));
        assertThat(campanha.getStatus()).isEqualTo("META_ATINGIDA");
        verify(campanhaDoacaoRepository, times(1)).save(campanha);
    }

    @Test
    void deveAtualizarStatusDoPaymentEAtualizarPedidoSeAprovado() {
        payment.setStatus(PaymentStatus.PENDING);
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setStatus(StatusPedido.PENDENTE);
        payment.setPedido(pedido);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Payment result = paymentService.updateStatus(payment, PaymentStatus.APPROVED);

        assertThat(result).isNotNull();
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.PAGO);
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void deveAtualizarStatusDoPaymentEAtualizarPedidoSeCancelado() {
        payment.setStatus(PaymentStatus.PENDING);
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setStatus(StatusPedido.PENDENTE);
        payment.setPedido(pedido);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Payment result = paymentService.updateStatus(payment, PaymentStatus.CANCELLED);

        assertThat(result).isNotNull();
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
        verify(pedidoRepository, times(1)).save(pedido);
    }

    // ===================== CREATE PAYMENT =====================

    @Test
    void deveLancarExcecaoAoCriarPagamentoParaCampanhaInexistente() {
        com.Mybuddy.Myb.Model.Usuario usuario = new com.Mybuddy.Myb.Model.Usuario();
        usuario.setId(1L);

        com.Mybuddy.Myb.DTO.PaymentRequestDTO request = new com.Mybuddy.Myb.DTO.PaymentRequestDTO(
                null, 10L, null, new BigDecimal("50.00"), "Doação"
        );

        when(campanhaDoacaoRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.createPayment(usuario, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Campanha de doação não encontrada: 10");

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void deveLancarExcecaoAoCriarPagamentoParaCampanhaNaoAtiva() {
        com.Mybuddy.Myb.Model.Usuario usuario = new com.Mybuddy.Myb.Model.Usuario();
        usuario.setId(1L);

        com.Mybuddy.Myb.DTO.PaymentRequestDTO request = new com.Mybuddy.Myb.DTO.PaymentRequestDTO(
                null, 10L, null, new BigDecimal("50.00"), "Doação"
        );

        CampanhaDoacao campanha = new CampanhaDoacao();
        campanha.setId(10L);
        campanha.setStatus("ENCERRADA");

        when(campanhaDoacaoRepository.findById(10L)).thenReturn(Optional.of(campanha));

        assertThatThrownBy(() -> paymentService.createPayment(usuario, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("A campanha de doação selecionada não está ativa.");

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void deveCriarPagamentoComSucessoQuandoCampanhaAtiva() throws Exception {
        com.Mybuddy.Myb.Model.Usuario usuario = new com.Mybuddy.Myb.Model.Usuario();
        usuario.setId(1L);

        com.Mybuddy.Myb.DTO.PaymentRequestDTO request = new com.Mybuddy.Myb.DTO.PaymentRequestDTO(
                null, 10L, null, new BigDecimal("50.00"), "Doação"
        );

        CampanhaDoacao campanha = new CampanhaDoacao();
        campanha.setId(10L);
        campanha.setStatus("ATIVA");

        when(campanhaDoacaoRepository.findById(10L)).thenReturn(Optional.of(campanha));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        com.mercadopago.resources.preference.Preference mockPreference = mock(com.mercadopago.resources.preference.Preference.class);
        when(mockPreference.getId()).thenReturn("pref-123");
        when(mockPreference.getInitPoint()).thenReturn("http://initpoint");

        try (org.mockito.MockedConstruction<com.mercadopago.client.preference.PreferenceClient> mocked =
                     mockConstruction(com.mercadopago.client.preference.PreferenceClient.class, (mock, context) -> {
                         when(mock.create(any(com.mercadopago.client.preference.PreferenceRequest.class)))
                                 .thenReturn(mockPreference);
                     })) {

            com.Mybuddy.Myb.DTO.PaymentCreationResult result = paymentService.createPayment(usuario, request);

            assertThat(result).isNotNull();
            assertThat(result.payment().getId()).isEqualTo(100L);
            assertThat(result.payment().getMpPreferenceId()).isEqualTo("pref-123");
            assertThat(result.initPoint()).isEqualTo("http://initpoint");
            verify(paymentRepository, times(1)).save(any(Payment.class));
        }
    }

    @Test
    void deveCriarPagamentoComSucessoQuandoNaoForCampanha() throws Exception {
        com.Mybuddy.Myb.Model.Usuario usuario = new com.Mybuddy.Myb.Model.Usuario();
        usuario.setId(1L);

        com.Mybuddy.Myb.DTO.PaymentRequestDTO request = new com.Mybuddy.Myb.DTO.PaymentRequestDTO(
                null, null, null, new BigDecimal("50.00"), "Doação Geral"
        );

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(200L);
            return p;
        });

        com.mercadopago.resources.preference.Preference mockPreference = mock(com.mercadopago.resources.preference.Preference.class);
        when(mockPreference.getId()).thenReturn("pref-456");
        when(mockPreference.getInitPoint()).thenReturn("http://initpoint-geral");

        try (org.mockito.MockedConstruction<com.mercadopago.client.preference.PreferenceClient> mocked =
                     mockConstruction(com.mercadopago.client.preference.PreferenceClient.class, (mock, context) -> {
                         when(mock.create(any(com.mercadopago.client.preference.PreferenceRequest.class)))
                                 .thenReturn(mockPreference);
                     })) {

            com.Mybuddy.Myb.DTO.PaymentCreationResult result = paymentService.createPayment(usuario, request);

            assertThat(result).isNotNull();
            assertThat(result.payment().getId()).isEqualTo(200L);
            assertThat(result.payment().getMpPreferenceId()).isEqualTo("pref-456");
            assertThat(result.initPoint()).isEqualTo("http://initpoint-geral");
            verify(paymentRepository, times(1)).save(any(Payment.class));
            verifyNoInteractions(campanhaDoacaoRepository);
        }
    }
}
