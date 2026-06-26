package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Config.TestSecurityConfig;
import com.Mybuddy.Myb.Model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestSecurityConfig.class)
public class MarketplaceRepositoryTest {

    @Autowired
    private PetshopRepository petshopRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SubCategoriaRepository subCategoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EnderecoEntregaRepository enderecoEntregaRepository;

    @Autowired
    private AvaliacaoProdutoRepository avaliacaoProdutoRepository;

    @Test
    public void deveSalvarEConsultarEntidadesDoMarketplace() {
        // 1. Criar e Salvar Petshop
        Petshop petshop = Petshop.builder()
                .nomeFantasia("Pet Shop Teste")
                .cnpj("11.222.333/0001-44")
                .emailContato("contato@petshopteste.com")
                .build();
        petshop = petshopRepository.save(petshop);
        assertThat(petshop.getId()).isNotNull();

        // 2. Criar e Salvar Categoria e SubCategoria
        Categoria categoria = Categoria.builder()
                .nome("Higiene e Beleza")
                .build();
        categoria = categoriaRepository.save(categoria);
        assertThat(categoria.getId()).isNotNull();

        SubCategoria subCategoria = SubCategoria.builder()
                .nome("Shampoo")
                .categoria(categoria)
                .build();
        subCategoria = subCategoriaRepository.save(subCategoria);
        assertThat(subCategoria.getId()).isNotNull();

        // 3. Criar e Salvar Produto
        Produto produto = new Produto();
        produto.setNome("Shampoo Cães Neutro 500ml");
        produto.setPreco(new BigDecimal("29.90"));
        produto.setEstoque(10);
        produto.setStatus(StatusProduto.ATIVO);
        produto.setPetshop(petshop);
        produto.setSubCategoria(subCategoria);
        produto = produtoRepository.save(produto);
        assertThat(produto.getId()).isNotNull();

        // 4. Criar e Salvar EnderecoEntrega e Pedido
        EnderecoEntrega endereco = EnderecoEntrega.builder()
                .cep("87000-000")
                .logradouro("Avenida Principal")
                .numero("100")
                .bairro("Centro")
                .cidade("Maringá")
                .estado("PR")
                .build();
        endereco = enderecoEntregaRepository.save(endereco);
        assertThat(endereco.getId()).isNotNull();

        Pedido pedido = new Pedido();
        pedido.setClienteId(1L); // ID fictício de adotante no MongoDB
        pedido.setPetshop(petshop);
        pedido.setEnderecoEntrega(endereco);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido = pedidoRepository.save(pedido);
        assertThat(pedido.getId()).isNotNull();

        // 5. Criar e Salvar Avaliacao
        AvaliacaoProduto avaliacao = AvaliacaoProduto.builder()
                .produto(produto)
                .clienteId(1L)
                .nota(5)
                .comentario("Excelente produto, muito perfumado!")
                .build();
        avaliacao = avaliacaoProdutoRepository.save(avaliacao);
        assertThat(avaliacao.getId()).isNotNull();

        // 6. Consultas e Verificações de Relacionamentos
        Optional<Produto> produtoConsultado = produtoRepository.findById(produto.getId());
        assertThat(produtoConsultado).isPresent();
        assertThat(produtoConsultado.get().getPetshop().getId()).isEqualTo(petshop.getId());
        assertThat(produtoConsultado.get().getSubCategoria().getId()).isEqualTo(subCategoria.getId());

        Optional<Pedido> pedidoConsultado = pedidoRepository.findById(pedido.getId());
        assertThat(pedidoConsultado).isPresent();
        assertThat(pedidoConsultado.get().getEnderecoEntrega().getId()).isEqualTo(endereco.getId());
        assertThat(pedidoConsultado.get().getPetshop().getId()).isEqualTo(petshop.getId());
    }
}
