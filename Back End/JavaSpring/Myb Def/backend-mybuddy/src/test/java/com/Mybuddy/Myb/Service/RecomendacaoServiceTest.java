package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.ProdutoResponseDTO;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecomendacaoServiceTest {

    @Mock
    private InteresseAdocaoRepository interesseRepo;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoService produtoService;

    private RecomendacaoService recomendacaoService;
    private Petshop petshop;
    private SubCategoria subCategoria;

    @BeforeEach
    void setUp() {
        recomendacaoService = new RecomendacaoService(interesseRepo, produtoRepository, produtoService);
        petshop = Petshop.builder()
                .id(1L)
                .nomeFantasia("Petshop Centro")
                .statusAprovacao(StatusAprovacao.APROVADO)
                .build();
        Categoria categoria = Categoria.builder().id(1L).nome("Produtos").build();
        subCategoria = SubCategoria.builder().id(1L).nome("Rações").categoria(categoria).build();
    }

    @Test
    void obterRecomendacoesParaUsuario_DevePriorizarPerfilDoPet() {
        Pet pet = new Pet();
        pet.setId(10L);
        pet.setEspecie(Especie.CAO);
        pet.setIdade(0);

        InteresseAdocao interesse = new InteresseAdocao();
        interesse.setPet(pet);

        Produto produtoFilhote = criarProduto(1L, "Ração para cão filhote", "Alimento canino puppy", 0);
        Produto produtoGato = criarProduto(2L, "Ração para gato adulto", "Alimento felino", 0);

        when(interesseRepo.findByUsuarioId(5L)).thenReturn(List.of(interesse));
        when(produtoRepository.findAll()).thenReturn(List.of(produtoGato, produtoFilhote));
        when(produtoService.mapToResponseDTO(any(Produto.class))).thenAnswer(invocation -> {
            Produto produto = invocation.getArgument(0);
            return ProdutoResponseDTO.builder().id(produto.getId()).nome(produto.getNome()).build();
        });

        List<ProdutoResponseDTO> response = recomendacaoService.obterRecomendacoesParaUsuario(5L);

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
    }

    @Test
    void obterRecomendacoesParaUsuario_SemPets_DeveUsarFallbackPorNotaERecencia() {
        Produto produtoNovo = criarProduto(1L, "Produto novo", "Adulto", 4);
        produtoNovo.setDataCriacao(LocalDateTime.now());

        Produto produtoMelhorAvaliado = criarProduto(2L, "Produto melhor avaliado", "Adulto", 5);
        produtoMelhorAvaliado.setDataCriacao(LocalDateTime.now().minusDays(5));

        when(interesseRepo.findByUsuarioId(5L)).thenReturn(List.of());
        when(produtoRepository.findAll()).thenReturn(List.of(produtoNovo, produtoMelhorAvaliado));
        when(produtoService.mapToResponseDTO(any(Produto.class))).thenAnswer(invocation -> {
            Produto produto = invocation.getArgument(0);
            return ProdutoResponseDTO.builder().id(produto.getId()).nome(produto.getNome()).build();
        });

        List<ProdutoResponseDTO> response = recomendacaoService.obterRecomendacoesParaUsuario(5L);

        assertEquals(2, response.size());
        assertEquals(2L, response.get(0).getId());
    }

    private Produto criarProduto(Long id, String nome, String descricao, int nota) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(new BigDecimal("29.90"));
        produto.setEstoque(10);
        produto.setStatus(StatusProduto.ATIVO);
        produto.setPetshop(petshop);
        produto.setSubCategoria(subCategoria);
        produto.setAvaliacoes(new HashSet<>());

        if (nota > 0) {
            AvaliacaoProduto avaliacao = new AvaliacaoProduto();
            avaliacao.setNota(nota);
            avaliacao.setProduto(produto);
            produto.getAvaliacoes().add(avaliacao);
        }

        return produto;
    }
}
