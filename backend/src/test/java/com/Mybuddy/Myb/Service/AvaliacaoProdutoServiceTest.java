package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.AvaliacaoProdutoRequestDTO;
import com.Mybuddy.Myb.DTO.AvaliacaoProdutoResponseDTO;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.AvaliacaoProduto;
import com.Mybuddy.Myb.Model.Produto;
import com.Mybuddy.Myb.Model.StatusPedido;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.jpa.AvaliacaoProdutoRepository;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoProdutoServiceTest {

    @Mock
    private AvaliacaoProdutoRepository avaliacaoProdutoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private AvaliacaoProdutoService avaliacaoProdutoService;

    private Usuario usuario;
    private Produto produto;
    private AvaliacaoProdutoRequestDTO request;
    private AvaliacaoProduto avaliacao;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eder");

        produto = new Produto();
        produto.setId(10L);
        produto.setNome("Ração Premium");

        request = new AvaliacaoProdutoRequestDTO();
        request.setNota(5);
        request.setComentario("Excelente produto!");

        avaliacao = AvaliacaoProduto.builder()
                .id(100L)
                .produto(produto)
                .clienteId(1L)
                .nota(5)
                .comentario("Excelente produto!")
                .build();
    }

    @Test
    void deveCriarAvaliacaoComSucessoQuandoCompraEntregue() {
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.existeCompraConcluida(1L, StatusPedido.ENTREGUE, 10L)).thenReturn(true);
        when(avaliacaoProdutoRepository.save(any(AvaliacaoProduto.class))).thenReturn(avaliacao);

        AvaliacaoProdutoResponseDTO result = avaliacaoProdutoService.criar(10L, request, usuario);

        assertThat(result).isNotNull();
        assertThat(result.getNota()).isEqualTo(5);
        assertThat(result.getComentario()).isEqualTo("Excelente produto!");
        verify(avaliacaoProdutoRepository, times(1)).save(any(AvaliacaoProduto.class));
    }

    @Test
    void deveLancarExcecaoAoAvaliarProdutoNaoComprado() {
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.existeCompraConcluida(1L, StatusPedido.ENTREGUE, 10L)).thenReturn(false);

        assertThatThrownBy(() -> avaliacaoProdutoService.criar(10L, request, usuario))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Você só pode avaliar produtos que foram entregues a você.");

        verify(avaliacaoProdutoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAvaliarProdutoInexistente() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> avaliacaoProdutoService.criar(99L, request, usuario))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Produto não encontrado com ID: 99");

        verify(pedidoRepository, never()).existeCompraConcluida(any(), any(), any());
    }

    @Test
    void deveListarAvaliacoesPorProduto() {
        when(produtoRepository.existsById(10L)).thenReturn(true);
        when(avaliacaoProdutoRepository.findByProdutoId(10L)).thenReturn(List.of(avaliacao));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        List<AvaliacaoProdutoResponseDTO> result = avaliacaoProdutoService.listarPorProduto(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClienteNome()).isEqualTo("Eder");
    }

    @Test
    void deveLancarExcecaoAoListarAvaliacoesDeProdutoInexistente() {
        when(produtoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> avaliacaoProdutoService.listarPorProduto(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Produto não encontrado com ID: 99");
    }
}
