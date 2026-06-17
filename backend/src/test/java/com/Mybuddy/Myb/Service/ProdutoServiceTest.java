package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.ProdutoRequestDTO;
import com.Mybuddy.Myb.DTO.ProdutoResponseDTO;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.jpa.FotoProdutoRepository;
import com.Mybuddy.Myb.Repository.jpa.PetshopRepository;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Repository.jpa.SubCategoriaRepository;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private SubCategoriaRepository subCategoriaRepository;

    @Mock
    private PetshopRepository petshopRepository;

    @Mock
    private FotoProdutoRepository fotoProdutoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private ProdutoRequestDTO requestDTO;
    private Usuario usuario;
    private Petshop petshop;
    private SubCategoria subCategoria;
    private Categoria categoria;
    private Produto produto;

    @BeforeEach
    void setUp() {
        requestDTO = new ProdutoRequestDTO("Ração Premier", "Ração super premium", new BigDecimal("150.00"), 10, 1L, List.of("url1", "url2"), null, null, null, null, null);
        
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setPetshopId(10L);

        petshop = Petshop.builder()
                .id(10L)
                .nomeFantasia("Petshop Ed")
                .statusAprovacao(StatusAprovacao.APROVADO)
                .build();

        categoria = Categoria.builder().id(5L).nome("Cães").build();
        subCategoria = SubCategoria.builder().id(1L).nome("Ração").categoria(categoria).build();

        produto = new Produto();
        produto.setId(20L);
        produto.setNome("Ração Premier");
        produto.setDescricao("Ração super premium");
        produto.setPreco(new BigDecimal("150.00"));
        produto.setEstoque(10);
        produto.setSubCategoria(subCategoria);
        produto.setPetshop(petshop);
        produto.setStatus(StatusProduto.ATIVO);
    }

    @Test
    void criarProduto_ComSucesso() {
        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(subCategoriaRepository.findById(1L)).thenReturn(Optional.of(subCategoria));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoResponseDTO response = produtoService.criar(requestDTO, usuario);

        assertNotNull(response);
        assertEquals(20L, response.getId());
        assertEquals("Ração Premier", response.getNome());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void criarProduto_SemPetshopCadastrado_DeveLancarExcecao() {
        usuario.setPetshopId(null);

        assertThrows(IllegalArgumentException.class, () -> produtoService.criar(requestDTO, usuario));
    }

    @Test
    void buscarProdutoPorId_ComSucesso() {
        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));

        ProdutoResponseDTO response = produtoService.buscarPorIdDTO(20L);

        assertNotNull(response);
        assertEquals(20L, response.getId());
    }

    @Test
    void buscarProdutoPorId_Inexistente_DeveLancarExcecao() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> produtoService.buscarPorIdDTO(99L));
    }

    @Test
    void atualizarProduto_ComSucesso() {
        Role petshopRole = new Role();
        petshopRole.setName(ERole.ROLE_PETSHOP);
        usuario.setRoles(Set.of(petshopRole));

        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));
        when(subCategoriaRepository.findById(1L)).thenReturn(Optional.of(subCategoria));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoResponseDTO response = produtoService.atualizar(20L, requestDTO, usuario);

        assertNotNull(response);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void atualizarProduto_SemPermissao_DeveLancarExcecao() {
        Role petshopRole = new Role();
        petshopRole.setName(ERole.ROLE_PETSHOP);
        usuario.setRoles(Set.of(petshopRole));
        usuario.setPetshopId(99L); // outro petshop

        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));

        assertThrows(AuthorizationDeniedException.class, () -> produtoService.atualizar(20L, requestDTO, usuario));
    }

    @Test
    void deletarProduto_ComSucesso() {
        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);
        usuario.setRoles(Set.of(adminRole));

        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produto));

        produtoService.deletar(20L, usuario);

        verify(produtoRepository, times(1)).delete(produto);
    }

    @Test
    void criarProduto_PetshopNaoAprovado_DeveLancarExcecao() {
        petshop.setStatusAprovacao(StatusAprovacao.PENDENTE_APROVACAO);
        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));

        assertThrows(AuthorizationDeniedException.class, () -> produtoService.criar(requestDTO, usuario));
        verify(produtoRepository, never()).save(any(Produto.class));
    }
}
