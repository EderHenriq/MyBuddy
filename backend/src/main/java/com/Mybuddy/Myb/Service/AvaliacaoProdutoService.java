package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.AvaliacaoProdutoRequestDTO;
import com.Mybuddy.Myb.DTO.AvaliacaoProdutoResponseDTO;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.AvaliacaoProduto;
import com.Mybuddy.Myb.Model.Produto;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Model.StatusPedido;
import com.Mybuddy.Myb.Repository.jpa.AvaliacaoProdutoRepository;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AvaliacaoProdutoService {

    private final AvaliacaoProdutoRepository avaliacaoProdutoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    /**
     * Registra a avaliação de um produto feita por um cliente que já teve uma compra entregue.
     *
     * @param produtoId identificador do produto avaliado
     * @param request dados da avaliação (nota e comentário)
     * @param usuario cliente autenticado que está avaliando
     * @return avaliação criada
     */
    @Transactional
    public AvaliacaoProdutoResponseDTO criar(Long produtoId, AvaliacaoProdutoRequestDTO request, Usuario usuario) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId));

        boolean comprou = pedidoRepository.existeCompraConcluida(usuario.getId(), StatusPedido.ENTREGUE, produtoId);
        if (!comprou) {
            throw new IllegalStateException("Você só pode avaliar produtos que foram entregues a você.");
        }

        AvaliacaoProduto avaliacao = AvaliacaoProduto.builder()
                .produto(produto)
                .clienteId(usuario.getId())
                .nota(request.getNota())
                .comentario(request.getComentario())
                .build();

        AvaliacaoProduto salvo = avaliacaoProdutoRepository.save(avaliacao);
        return toResponseDTO(salvo, usuario.getNome());
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoProdutoResponseDTO> listarPorProduto(Long produtoId) {
        if (!produtoRepository.existsById(produtoId)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId);
        }

        return avaliacaoProdutoRepository.findByProdutoId(produtoId).stream()
                .map(av -> {
                    String clienteNome = usuarioRepository.findById(av.getClienteId())
                            .map(Usuario::getNome)
                            .orElse("Usuário do MyBuddy");
                    return toResponseDTO(av, clienteNome);
                })
                .collect(Collectors.toList());
    }

    private AvaliacaoProdutoResponseDTO toResponseDTO(AvaliacaoProduto av, String clienteNome) {
        return AvaliacaoProdutoResponseDTO.builder()
                .id(av.getId())
                .produtoId(av.getProduto().getId())
                .clienteId(av.getClienteId())
                .clienteNome(clienteNome)
                .nota(av.getNota())
                .comentario(av.getComentario())
                .dataCriacao(av.getDataCriacao())
                .build();
    }
}
