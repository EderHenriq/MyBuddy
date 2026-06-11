package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.*;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.jpa.PetshopRepository;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        requestDTO = new PedidoRequestDTO(10L, enderecoDTO, List.of(itemDTO));

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setPetshopId(10L);

        petshop = Petshop.builder()
                .id(10L)
                .nomeFantasia("Petshop Ed")
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

        int estoqueAntes = produto.getEstoque();

        PedidoResponseDTO response = pedidoService.cancelar(30L, usuario);

        assertNotNull(response);
        assertEquals(StatusPedido.CANCELADO.name(), response.getStatus());
        assertEquals(estoqueAntes + 2, produto.getEstoque()); // devolveu 2 itens
        verify(produtoRepository, times(1)).save(produto);
    }
}
