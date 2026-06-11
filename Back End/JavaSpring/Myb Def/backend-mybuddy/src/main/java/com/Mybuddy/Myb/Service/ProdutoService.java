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
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final SubCategoriaRepository subCategoriaRepository;
    private final PetshopRepository petshopRepository;
    private final FotoProdutoRepository fotoProdutoRepository;

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> buscarComFiltros(
            String busca, Long categoriaId, Long subCategoriaId, Long petshopId,
            BigDecimal precoMin, BigDecimal precoMax, Pageable pageable) {

        Specification<Produto> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Apenas produtos ativos por padrão na busca pública
            predicates.add(cb.equal(root.get("status"), StatusProduto.ATIVO));

            if (busca != null && !busca.isBlank()) {
                String term = "%" + busca.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nome")), term),
                        cb.like(cb.lower(root.get("descricao")), term)
                ));
            }

            if (subCategoriaId != null) {
                predicates.add(cb.equal(root.get("subCategoria").get("id"), subCategoriaId));
            } else if (categoriaId != null) {
                Join<Produto, SubCategoria> subCatJoin = root.join("subCategoria");
                predicates.add(cb.equal(subCatJoin.get("categoria").get("id"), categoriaId));
            }

            if (petshopId != null) {
                predicates.add(cb.equal(root.get("petshop").get("id"), petshopId));
            }

            if (precoMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("preco"), precoMin));
            }

            if (precoMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("preco"), precoMax));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return produtoRepository.findAll(spec, pageable).map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> buscarPorPetshop(Long petshopId, Pageable pageable) {
        Specification<Produto> spec = (root, query, cb) -> cb.equal(root.get("petshop").get("id"), petshopId);
        return produtoRepository.findAll(spec, pageable).map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorIdDTO(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
        return toResponseDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO request, Usuario usuario) {
        if (usuario.getPetshopId() == null) {
            throw new IllegalArgumentException("O usuário logado não possui um petshop cadastrado.");
        }

        Petshop petshop = petshopRepository.findById(usuario.getPetshopId())
                .orElseThrow(() -> new ResourceNotFoundException("Petshop associado ao usuário não encontrado."));

        SubCategoria subCategoria = subCategoriaRepository.findById(request.getSubCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoria não encontrada com ID: " + request.getSubCategoriaId()));

        Produto produto = new Produto();
        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setEstoque(request.getEstoque());
        produto.setSubCategoria(subCategoria);
        produto.setPetshop(petshop);
        produto.setStatus(StatusProduto.ATIVO);

        Produto salvo = produtoRepository.save(produto);

        if (request.getImagens() != null) {
            for (String url : request.getImagens()) {
                FotoProduto foto = new FotoProduto();
                foto.setUrl(url);
                foto.setProduto(salvo);
                fotoProdutoRepository.save(foto);
                salvo.addFoto(foto);
            }
        }

        return toResponseDTO(salvo);
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO request, Usuario usuario) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        if (!isAdmin && !produto.getPetshop().getId().equals(usuario.getPetshopId())) {
            throw new AuthorizationDeniedException("Você não tem permissão para alterar este produto.");
        }

        SubCategoria subCategoria = subCategoriaRepository.findById(request.getSubCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoria não encontrada com ID: " + request.getSubCategoriaId()));

        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setEstoque(request.getEstoque());
        produto.setSubCategoria(subCategoria);

        // Atualizar fotos se enviado
        if (request.getImagens() != null) {
            fotoProdutoRepository.deleteByProdutoId(id);
            produto.getFotos().clear();
            for (String url : request.getImagens()) {
                FotoProduto foto = new FotoProduto();
                foto.setUrl(url);
                foto.setProduto(produto);
                fotoProdutoRepository.save(foto);
                produto.addFoto(foto);
            }
        }

        return toResponseDTO(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(Long id, Usuario usuario) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        if (!isAdmin && !produto.getPetshop().getId().equals(usuario.getPetshopId())) {
            throw new AuthorizationDeniedException("Você não tem permissão para deletar este produto.");
        }

        produtoRepository.delete(produto);
    }

    private ProdutoResponseDTO toResponseDTO(Produto p) {
        List<String> fotos = p.getFotos() == null ? Collections.emptyList() :
                p.getFotos().stream()
                        .map(FotoProduto::getUrl)
                        .collect(Collectors.toList());

        double media = p.getAvaliacoes() == null || p.getAvaliacoes().isEmpty() ? 0.0 :
                p.getAvaliacoes().stream()
                        .mapToInt(AvaliacaoProduto::getNota)
                        .average()
                        .orElse(0.0);

        return ProdutoResponseDTO.builder()
                .id(p.getId())
                .nome(p.getNome())
                .descricao(p.getDescricao())
                .preco(p.getPreco())
                .estoque(p.getEstoque())
                .status(p.getStatus().name())
                .subCategoriaId(p.getSubCategoria().getId())
                .subCategoriaNome(p.getSubCategoria().getNome())
                .categoriaId(p.getSubCategoria().getCategoria().getId())
                .categoriaNome(p.getSubCategoria().getCategoria().getNome())
                .petshopId(p.getPetshop().getId())
                .petshopNome(p.getPetshop().getNomeFantasia())
                .imagens(fotos)
                .notaMedia(media)
                .build();
    }
}
