package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.*;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.jpa.PetshopRepository;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Security.ERole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final PetshopRepository petshopRepository;

    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO request, Usuario usuario) {
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

        pedido.setValorTotal(total);
        Pedido salvo = pedidoRepository.save(pedido);

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

        return toResponseDTO(pedidoRepository.save(pedido));
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

        pedido.setStatus(StatusPedido.CANCELADO);
        devolverEstoque(pedido);

        return toResponseDTO(pedidoRepository.save(pedido));
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
                .status(p.getStatus().name())
                .dataCriacao(p.getDataCriacao())
                .dataAtualizacao(p.getDataAtualizacao())
                .build();
    }
}
