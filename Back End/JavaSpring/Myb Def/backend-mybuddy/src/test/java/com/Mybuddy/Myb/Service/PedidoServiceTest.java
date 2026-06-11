package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.*;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.jpa.PetshopRepository;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Repository.jpa.CupomRepository;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.mercadopago.client.payment.PaymentRefundClient;
import com.Mybuddy.Myb.Repository.jpa.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PetshopRepository petshopRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CupomRepository cupomRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private CupomService cupomService;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private PedidoRequestDTO requestDTO;
    private Usuario usuario;
    private Petshop petshop;
    private Produto produto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        EnderecoEntregaDTO enderecoDTO = new EnderecoEntregaDTO("87000-000", "Rua Teste", "123", "Ap 1", "Bairro", "Maringá", "PR");
        ItemPedidoRequestDTO itemDTO = new ItemPedidoRequestDTO(20L, 2);
        requestDTO = new PedidoRequestDTO(10L, enderecoDTO, List.of(itemDTO), null);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setPetshopId(10L);
        usuario.setEmail("cliente@gmail.com");
        usuario.setNome("João Adotante");
        Role role = new Role();
        role.setName(ERole.ROLE_ADOTANTE);
        usuario.setRoles(Set.of(role));

        petshop = Petshop.builder()
                .id(10L)
                .nomeFantasia("Petshop Ed")
                .valorMinimoFreteGratis(new BigDecimal("150.00"))
                .build();

        produto = new Produto();
        produto.setId(20L);
        produto.setNome("Ração");
        produto.setPreco(new BigDecimal("100.00"));
        produto.setEstoque(5);
        produto.setStatus(StatusProduto.ATIVO);
        produto.setPetshop(petshop);

        pedido = new Pedido();
        pedido.setId(30L);
        pedido.setClienteId(1L);
        pedido.setPetshop(petshop);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setEnderecoEntrega(EnderecoEntrega.builder().cep("87000-000").build());

        ItemPedido item = new ItemPedido();
        item.setId(40L);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("100.00"));
        pedido.addItem(item);
    }

    @Test
    void criarPedido_ComSucesso_DeveReduzirEstoque() {
        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoResponseDTO response = pedidoService.criar(requestDTO, usuario);

        assertNotNull(response);
        assertEquals(3, produto.getEstoque()); // 5 - 2
        verify(produtoRepository, times(1)).save(produto);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void criarPedido_EstoqueInsuficiente_DeveLancarExcecao() {
        produto.setEstoque(1); // menor que os 2 solicitados

        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));

        assertThrows(IllegalArgumentException.class, () -> pedidoService.criar(requestDTO, usuario));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void atualizarStatus_WorkflowCorreto() {
        Role petshopRole = new Role();
        petshopRole.setName(ERole.ROLE_PETSHOP);
        usuario.setRoles(Set.of(petshopRole));

        when(pedidoRepository.findById(30L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        PedidoResponseDTO response = pedidoService.atualizarStatus(30L, StatusPedido.PAGO, usuario);

        assertNotNull(response);
        assertEquals(StatusPedido.PAGO.name(), response.getStatus());
    }

    @Test
    void atualizarStatus_WorkflowIncorreto_DeveLancarExcecao() {
        Role petshopRole = new Role();
        petshopRole.setName(ERole.ROLE_PETSHOP);
        usuario.setRoles(Set.of(petshopRole));

        when(pedidoRepository.findById(30L)).thenReturn(Optional.of(pedido));

        // Transição inválida: PENDENTE para ENTREGUE direto
        assertThrows(IllegalArgumentException.class, () -> pedidoService.atualizarStatus(30L, StatusPedido.ENTREGUE, usuario));
    }

    @Test
    void cancelarPedido_DevolveEstoque_PendenteParaCancelado() {
        when(pedidoRepository.findById(30L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        int estoqueAntes = produto.getEstoque();

        PedidoResponseDTO response = pedidoService.cancelar(30L, usuario);

        assertNotNull(response);
        assertEquals(StatusPedido.CANCELADO.name(), response.getStatus());
        assertEquals(estoqueAntes + 2, produto.getEstoque()); // devolveu 2 itens
        verify(produtoRepository, times(1)).save(produto);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void criarPedido_SemRoleAdotante_DeveLancarExcecao() {
        usuario.setRoles(Collections.emptySet()); // remove roles

        assertThrows(AuthorizationDeniedException.class, () -> pedidoService.criar(requestDTO, usuario));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void criarPedido_ComFretePadrao_CEPInteriorSP() {
        // CEP começa com "1" -> R$ 10.00
        requestDTO.getEnderecoEntrega().setCep("13000-000");
        petshop.setValorMinimoFreteGratis(new BigDecimal("300.00")); // Acima do subtotal (200.00)

        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(30L);
            return p;
        });

        PedidoResponseDTO response = pedidoService.criar(requestDTO, usuario);

        assertNotNull(response);
        assertEquals(new BigDecimal("10.00"), response.getValorFrete());
        assertEquals(new BigDecimal("210.00"), response.getValorTotal()); // 200.00 subtotal + 10.00 frete
        verify(emailService, times(1)).enviarEmail(any(), any(), any());
    }

    @Test
    void criarPedido_ComFreteGratis_SubtotalAtingeLimite() {
        // Subtotal = R$ 200.00 (2 itens de 100.00)
        petshop.setValorMinimoFreteGratis(new BigDecimal("150.00")); // Menor que o subtotal

        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(30L);
            return p;
        });

        PedidoResponseDTO response = pedidoService.criar(requestDTO, usuario);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.getValorFrete()); // Frete grátis!
        assertEquals(new BigDecimal("200.00"), response.getValorTotal());
    }

    @Test
    void criarPedido_ComCupomValido_PercentualDesconto() {
        requestDTO.setCupomDesconto("DESCONTO15");
        petshop.setValorMinimoFreteGratis(new BigDecimal("300.00")); // Sem frete grátis automático
        requestDTO.getEnderecoEntrega().setCep("13000-000"); // Frete R$ 10.00

        Cupom cupomDesconto = Cupom.builder()
                .codigo("DESCONTO15")
                .percentualDesconto(new BigDecimal("15.00")) // 15% de desconto
                .petshop(petshop)
                .ativo(true)
                .build();

        CupomResponseDTO cupomDTO = CupomResponseDTO.builder()
                .id(1L)
                .codigo("DESCONTO15")
                .percentualDesconto(new BigDecimal("15.00"))
                .petshopId(petshop.getId())
                .ativo(true)
                .build();

        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));
        when(cupomService.buscarPorCodigoValido("DESCONTO15", 10L, 1L, new BigDecimal("200.00"))).thenReturn(cupomDTO);
        when(cupomRepository.findByCodigoAndAtivoTrue("DESCONTO15")).thenReturn(Optional.of(cupomDesconto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(30L);
            return p;
        });

        PedidoResponseDTO response = pedidoService.criar(requestDTO, usuario);

        assertNotNull(response);
        // Subtotal: 200.00. Desconto 15%: 30.00. Frete: 10.00. Total: 200 + 10 - 30 = 180.00
        assertEquals(new BigDecimal("30.00"), response.getValorDesconto());
        assertEquals(new BigDecimal("10.00"), response.getValorFrete());
        assertEquals(new BigDecimal("180.00"), response.getValorTotal());
    }

    @Test
    void criarPedido_ComCupomIncompativelPetshop_DeveLancarExcecao() {
        requestDTO.setCupomDesconto("OUTROPETSHOP");

        Petshop outroPetshop = Petshop.builder().id(999L).build();
        Cupom cupomIncompativel = Cupom.builder()
                .codigo("OUTROPETSHOP")
                .percentualDesconto(new BigDecimal("10.00"))
                .petshop(outroPetshop)
                .ativo(true)
                .build();

        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));
        when(cupomService.buscarPorCodigoValido("OUTROPETSHOP", 10L, 1L, new BigDecimal("200.00")))
                .thenThrow(new IllegalArgumentException("Cupom não pertence a este petshop."));

        assertThrows(IllegalArgumentException.class, () -> pedidoService.criar(requestDTO, usuario));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void criarPedido_ComCupomLegadoFreteGratis() {
        requestDTO.setCupomDesconto("FRETEGRATIS");
        requestDTO.getEnderecoEntrega().setCep("13000-000"); // Seria R$ 10.00
        petshop.setValorMinimoFreteGratis(new BigDecimal("300.00")); // Acima do subtotal

        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(30L);
            return p;
        });

        PedidoResponseDTO response = pedidoService.criar(requestDTO, usuario);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.getValorFrete()); // Cupom FRETEGRATIS zerou o frete
        assertEquals(BigDecimal.ZERO, response.getValorDesconto());
        assertEquals(new BigDecimal("200.00"), response.getValorTotal());
    }

    @Test
    void cancelarPedidoExpirado_ComSucesso_DeveAlterarStatusEDevolverEstoque() {
        int estoqueAntes = produto.getEstoque();

        pedidoService.cancelarPedidoExpirado(pedido);

        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
        assertEquals(estoqueAntes + 2, produto.getEstoque());
        verify(produtoRepository, times(1)).save(produto);
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void cancelarPedido_PedidoPagoComPagamentoAssociado_DeveDispararReembolso() throws Exception {
        pedido.setStatus(StatusPedido.PAGO);
        
        Payment payment = new Payment();
        payment.setId(50L);
        payment.setMpPaymentId("987654321");
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setAmount(new BigDecimal("200.00"));
        payment.setPedido(pedido);

        when(pedidoRepository.findById(30L)).thenReturn(Optional.of(pedido));
        when(paymentRepository.findByPedidoId(30L)).thenReturn(List.of(payment));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        try (MockedConstruction<PaymentRefundClient> mocked = Mockito.mockConstruction(PaymentRefundClient.class,
                (mock, context) -> {
                    // Não lança exceção ao chamar refund
                })) {

            PedidoResponseDTO response = pedidoService.cancelar(30L, usuario);

            assertNotNull(response);
            assertEquals(StatusPedido.CANCELADO.name(), response.getStatus());
            assertEquals(PaymentStatus.REFUNDED, payment.getStatus());
            verify(paymentRepository, times(1)).save(payment);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }
    }

    @Test
    void cancelarPedido_PedidoPagoSemPagamentoAssociado_DeveApenasCancelar() {
        pedido.setStatus(StatusPedido.PAGO);

        when(pedidoRepository.findById(30L)).thenReturn(Optional.of(pedido));
        when(paymentRepository.findByPedidoId(30L)).thenReturn(List.of());
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        PedidoResponseDTO response = pedidoService.cancelar(30L, usuario);

        assertNotNull(response);
        assertEquals(StatusPedido.CANCELADO.name(), response.getStatus());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void cancelarPedido_FalhaAoReembolsar_DeveLancarExcecao() {
        pedido.setStatus(StatusPedido.PAGO);
        
        Payment payment = new Payment();
        payment.setId(50L);
        payment.setMpPaymentId("987654321");
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setAmount(new BigDecimal("200.00"));
        payment.setPedido(pedido);

        when(pedidoRepository.findById(30L)).thenReturn(Optional.of(pedido));
        when(paymentRepository.findByPedidoId(30L)).thenReturn(List.of(payment));

        try (MockedConstruction<PaymentRefundClient> mocked = Mockito.mockConstruction(PaymentRefundClient.class,
                (mock, context) -> {
                    doThrow(new RuntimeException("API Error")).when(mock).refund(anyLong());
                })) {

            assertThrows(RuntimeException.class, () -> pedidoService.cancelar(30L, usuario));
            
            // O pagamento não deve ser marcado como REFUNDED e o pedido não deve ser cancelado
            assertEquals(PaymentStatus.APPROVED, payment.getStatus());
            verify(paymentRepository, never()).save(payment);
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }
    }
}
