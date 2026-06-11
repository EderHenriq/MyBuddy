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
import com.mercadopago.client.payment.PaymentRefundClient;
import com.Mybuddy.Myb.Repository.jpa.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final PetshopRepository petshopRepository;
    private final UsuarioRepository usuarioRepository;
    private final CupomRepository cupomRepository;
    private final CupomService cupomService;
    private final EmailService emailService;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO request, Usuario usuario) {
        boolean isAdotante = usuario.getRoles() != null && usuario.getRoles().stream()
                .anyMatch(r -> r.getName() == ERole.ROLE_ADOTANTE);
        if (!isAdotante) {
            throw new AuthorizationDeniedException("Apenas adotantes podem realizar compras no marketplace.");
        }

        Petshop petshop = petshopRepository.findById(request.getPetshopId())
                .orElseThrow(() -> new ResourceNotFoundException("Petshop não encontrado com ID: " + request.getPetshopId()));

        Pedido pedido = new Pedido();
        pedido.setClienteId(usuario.getId());
        pedido.setPetshop(petshop);
        pedido.setStatus(StatusPedido.PENDENTE);

        // Mapear EnderecoEntrega
        EnderecoEntrega endereco = EnderecoEntrega.builder()
                .cep(request.getEnderecoEntrega().getCep())
                .logradouro(request.getEnderecoEntrega().getLogradouro())
                .numero(request.getEnderecoEntrega().getNumero())
                .complemento(request.getEnderecoEntrega().getComplemento())
                .bairro(request.getEnderecoEntrega().getBairro())
                .cidade(request.getEnderecoEntrega().getCidade())
                .estado(request.getEnderecoEntrega().getEstado())
                .build();
        pedido.setEnderecoEntrega(endereco);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoRequestDTO itemReq : request.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + itemReq.getProdutoId()));

            if (produto.getStatus() != StatusProduto.ATIVO) {
                throw new IllegalArgumentException("O produto '" + produto.getNome() + "' não está ativo para venda.");
            }

            if (!produto.getPetshop().getId().equals(petshop.getId())) {
                throw new IllegalArgumentException("O produto '" + produto.getNome() + "' não pertence ao Petshop selecionado.");
            }

            if (produto.getEstoque() < itemReq.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto '" + produto.getNome() + "'. Disponível: " + produto.getEstoque());
            }

            // Decrementar estoque
            produto.setEstoque(produto.getEstoque() - itemReq.getQuantidade());
            if (produto.getEstoque() == 0) {
                produto.setStatus(StatusProduto.ESGOTADO);
            }
            produtoRepository.save(produto);

            // Criar ItemPedido
            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            pedido.addItem(item);
            total = total.add(item.getSubtotal());
        }

        BigDecimal frete = calcularFrete(request.getEnderecoEntrega().getCep(), total, petshop);
        pedido.setValorFrete(frete);

        // Aplica cupom com validação completa anti-abuso (validade, limite, uso único, valor mínimo)
        Long cupomIdAplicado = aplicarCupomEDesconto(
                pedido, request.getCupomDesconto(), total, frete, usuario.getId(), petshop.getId());

        BigDecimal valorTotalFinal = total.add(pedido.getValorFrete()).subtract(pedido.getValorDesconto());
        if (valorTotalFinal.compareTo(BigDecimal.ZERO) < 0) {
            valorTotalFinal = BigDecimal.ZERO;
        }
        pedido.setValorTotal(valorTotalFinal);

        Pedido salvo = pedidoRepository.save(pedido);

        // Registra o uso do cupom APÓS o pedido ser persistido com sucesso
        if (cupomIdAplicado != null) {
            cupomService.registrarUso(cupomIdAplicado, usuario.getId(), salvo.getId());
        }

        if (usuario.getEmail() != null) {
            emailService.enviarEmail(
                usuario.getEmail(),
                "Pedido #" + salvo.getId() + " criado com sucesso!",
                "Olá " + usuario.getNome() + ",\n\nSeu pedido #" + salvo.getId() + " de valor total R$ " + salvo.getValorTotal() + " foi registrado com sucesso."
            );
        }

        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorIdDTO(Long id, Usuario usuario) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));

        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        boolean isOwner = pedido.getClienteId().equals(usuario.getId());
        boolean isSeller = pedido.getPetshop().getId().equals(usuario.getPetshopId());

        if (!isAdmin && !isOwner && !isSeller) {
            throw new AuthorizationDeniedException("Você não tem permissão para visualizar este pedido.");
        }

        return toResponseDTO(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPedidosCliente(Usuario usuario) {
        return pedidoRepository.findByClienteId(usuario.getId()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPedidosPetshop(Usuario usuario) {
        if (usuario.getPetshopId() == null) {
            throw new IllegalArgumentException("O usuário não possui um petshop cadastrado.");
        }
        return pedidoRepository.findByPetshopId(usuario.getPetshopId()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, StatusPedido novoStatus, Usuario usuario) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));

        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        boolean isSeller = pedido.getPetshop().getId().equals(usuario.getPetshopId());

        if (!isAdmin && !isSeller) {
            throw new AuthorizationDeniedException("Você não tem permissão para atualizar o status deste pedido.");
        }

        validarTransicaoStatus(pedido.getStatus(), novoStatus);

        pedido.setStatus(novoStatus);

        if (novoStatus == StatusPedido.CANCELADO) {
            devolverEstoque(pedido);
        }

        Pedido salvo = pedidoRepository.save(pedido);

        Usuario cliente = usuarioRepository.findById(salvo.getClienteId()).orElse(null);
        if (cliente != null && cliente.getEmail() != null) {
            emailService.enviarEmail(
                cliente.getEmail(),
                "Atualização do seu pedido #" + salvo.getId(),
                "Olá " + cliente.getNome() + ",\n\nO status do seu pedido #" + salvo.getId() + " foi atualizado para: " + novoStatus.name() + "."
            );
        }

        return toResponseDTO(salvo);
    }

    @Transactional
    public PedidoResponseDTO cancelar(Long id, Usuario usuario) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));

        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        boolean isOwner = pedido.getClienteId().equals(usuario.getId());
        boolean isSeller = pedido.getPetshop().getId().equals(usuario.getPetshopId());

        if (!isAdmin && !isOwner && !isSeller) {
            throw new AuthorizationDeniedException("Você não tem permissão para cancelar este pedido.");
        }

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalStateException("O pedido já está cancelado.");
        }

        // Regra aprovada: Só pode cancelar se estiver PENDENTE ou PAGO
        if (pedido.getStatus() != StatusPedido.PENDENTE && pedido.getStatus() != StatusPedido.PAGO) {
            throw new IllegalStateException("Não é possível cancelar um pedido que já está " + pedido.getStatus().name() + ".");
        }

        if (pedido.getStatus() == StatusPedido.PAGO) {
            // Reembolsar no Mercado Pago se houver pagamento associado
            List<Payment> payments = paymentRepository.findByPedidoId(pedido.getId());
            Optional<Payment> approvedPaymentOpt = payments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.APPROVED && p.getMpPaymentId() != null)
                    .findFirst();

            if (approvedPaymentOpt.isPresent()) {
                Payment payment = approvedPaymentOpt.get();
                try {
                    log.info("Disparando reembolso no Mercado Pago para o pagamento ID: {}", payment.getMpPaymentId());
                    PaymentRefundClient refundClient = new PaymentRefundClient();
                    refundClient.refund(Long.parseLong(payment.getMpPaymentId()));
                    
                    // Atualiza localmente o pagamento para REFUNDED
                    payment.setStatus(PaymentStatus.REFUNDED);
                    paymentRepository.save(payment);
                    log.info("Reembolso executado e pagamento atualizado localmente para REFUNDED.");
                } catch (Exception e) {
                    log.error("Erro ao realizar reembolso automático no Mercado Pago para o pagamento {}: {}", 
                            payment.getMpPaymentId(), e.getMessage(), e);
                    throw new RuntimeException("Falha ao processar o estorno do pagamento no Mercado Pago. Detalhes: " + e.getMessage());
                }
            } else {
                log.warn("Pedido #{} está PAGO, mas nenhum pagamento APROVADO foi encontrado para estorno.", pedido.getId());
            }
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        devolverEstoque(pedido);

        Pedido salvo = pedidoRepository.save(pedido);

        Usuario cliente = usuarioRepository.findById(salvo.getClienteId()).orElse(null);
        if (cliente != null && cliente.getEmail() != null) {
            emailService.enviarEmail(
                cliente.getEmail(),
                "Cancelamento do seu pedido #" + salvo.getId(),
                "Olá " + cliente.getNome() + ",\n\nO seu pedido #" + salvo.getId() + " foi cancelado com sucesso."
            );
        }

        return toResponseDTO(salvo);
    }

    @Transactional
    public void cancelarPedidoExpirado(Pedido pedido) {
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            return;
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        devolverEstoque(pedido);
        pedidoRepository.save(pedido);

        Usuario cliente = usuarioRepository.findById(pedido.getClienteId()).orElse(null);
        if (cliente != null && cliente.getEmail() != null) {
            emailService.enviarEmail(
                cliente.getEmail(),
                "Cancelamento automático do seu pedido #" + pedido.getId(),
                "Olá " + cliente.getNome() + ",\n\nO seu pedido #" + pedido.getId() + " foi cancelado automaticamente por falta de pagamento."
            );
        }
    }

    private void devolverEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
            if (produto.getStatus() == StatusProduto.ESGOTADO) {
                produto.setStatus(StatusProduto.ATIVO);
            }
            produtoRepository.save(produto);
        }
    }

    private void validarTransicaoStatus(StatusPedido atual, StatusPedido novo) {
        if (atual == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException("Não é possível alterar o status de um pedido cancelado.");
        }
        if (atual == StatusPedido.ENTREGUE) {
            throw new IllegalArgumentException("Não é possível alterar o status de um pedido já entregue.");
        }

        boolean valida = false;
        switch (atual) {
            case PENDENTE:
                valida = (novo == StatusPedido.PAGO || novo == StatusPedido.CANCELADO);
                break;
            case PAGO:
                valida = (novo == StatusPedido.PROCESSANDO || novo == StatusPedido.CANCELADO);
                break;
            case PROCESSANDO:
                valida = (novo == StatusPedido.ENVIADO || novo == StatusPedido.CANCELADO);
                break;
            case ENVIADO:
                valida = (novo == StatusPedido.ENTREGUE); // Se já foi enviado, não pode cancelar diretamente via transição normal
                break;
            default:
                break;
        }

        if (!valida) {
            throw new IllegalArgumentException("Transição de status inválida de " + atual.name() + " para " + novo.name() + ".");
        }
    }

    private BigDecimal calcularFrete(String cep, BigDecimal subtotal, Petshop petshop) {
        if (petshop.getValorMinimoFreteGratis() != null 
                && subtotal.compareTo(petshop.getValorMinimoFreteGratis()) >= 0) {
            return BigDecimal.ZERO;
        }

        if (cep == null || cep.trim().isEmpty()) {
            return new BigDecimal("20.00");
        }

        String cepLimpo = cep.replaceAll("\\D", "");
        if (cepLimpo.isEmpty()) {
            return new BigDecimal("20.00");
        }

        char primeiroDigito = cepLimpo.charAt(0);
        switch (primeiroDigito) {
            case '0':
            case '1':
                return new BigDecimal("10.00");
            case '2':
                return new BigDecimal("15.00");
            case '3':
                return new BigDecimal("12.00");
            case '8':
            case '9':
                return new BigDecimal("12.00");
            default:
                return new BigDecimal("20.00");
        }
    }

    /**
     * Aplica cupom ao pedido com validação completa anti-abuso via CupomService.
     * Retorna o ID do cupom da tabela cupons (para registrar o uso após salvar o pedido),
     * ou null quando não foi aplicado nenhum cupom do banco.
     */
    private Long aplicarCupomEDesconto(Pedido pedido, String cupom, BigDecimal subtotal,
                                        BigDecimal freteOriginal, Long usuarioId, Long petshopId) {
        if (cupom == null || cupom.trim().isEmpty()) {
            pedido.setValorFrete(freteOriginal);
            pedido.setValorDesconto(BigDecimal.ZERO);
            return null;
        }

        String cupomFormatado = cupom.trim().toUpperCase();

        // Cupom especial FRETEGRATIS (não requer registro no banco)
        if (cupomFormatado.equals("FRETEGRATIS")) {
            pedido.setCupomDesconto(cupomFormatado);
            pedido.setValorFrete(BigDecimal.ZERO);
            pedido.setValorDesconto(BigDecimal.ZERO);
            return null;
        }

        // Delega a validação completa (validade, limite, uso único, petshop, valor mínimo) ao CupomService
        CupomResponseDTO cupomDTO = cupomService.buscarPorCodigoValido(
                cupomFormatado, petshopId, usuarioId, subtotal);

        // Recupera a entidade para calcular o desconto
        Cupom dbCupom = cupomRepository.findByCodigoAndAtivoTrue(cupomFormatado)
                .orElseThrow(() -> new IllegalArgumentException("Cupom inválido ou inativo."));

        pedido.setCupomDesconto(dbCupom.getCodigo());
        pedido.setValorFrete(freteOriginal);

        BigDecimal percentual = dbCupom.getPercentualDesconto();
        BigDecimal desconto = subtotal.multiply(percentual)
                .divide(new BigDecimal("100.00"), 2, java.math.RoundingMode.HALF_UP);
        pedido.setValorDesconto(desconto);

        return dbCupom.getId();
    }

    private PedidoResponseDTO toResponseDTO(Pedido p) {
        List<ItemPedidoResponseDTO> itens = p.getItens().stream()
                .map(item -> ItemPedidoResponseDTO.builder()
                        .produtoId(item.getProduto().getId())
                        .produtoNome(item.getProduto().getNome())
                        .quantidade(item.getQuantidade())
                        .precoUnitario(item.getPrecoUnitario())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        EnderecoEntregaDTO endereco = EnderecoEntregaDTO.builder()
                .cep(p.getEnderecoEntrega().getCep())
                .logradouro(p.getEnderecoEntrega().getLogradouro())
                .numero(p.getEnderecoEntrega().getNumero())
                .complemento(p.getEnderecoEntrega().getComplemento())
                .bairro(p.getEnderecoEntrega().getBairro())
                .cidade(p.getEnderecoEntrega().getCidade())
                .estado(p.getEnderecoEntrega().getEstado())
                .build();

        return PedidoResponseDTO.builder()
                .id(p.getId())
                .clienteId(p.getClienteId())
                .clienteNome(null) // Pode ser preenchido por quem consome buscando no mongo se necessário
                .petshopId(p.getPetshop().getId())
                .petshopNome(p.getPetshop().getNomeFantasia())
                .enderecoEntrega(endereco)
                .itens(itens)
                .valorTotal(p.getValorTotal())
                .valorFrete(p.getValorFrete())
                .cupomDesconto(p.getCupomDesconto())
                .valorDesconto(p.getValorDesconto())
                .status(p.getStatus().name())
                .dataCriacao(p.getDataCriacao())
                .dataAtualizacao(p.getDataAtualizacao())
                .build();
    }
}
