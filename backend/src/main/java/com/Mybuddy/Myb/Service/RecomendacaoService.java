package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.ProdutoResponseDTO;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecomendacaoService {

    private final InteresseAdocaoRepository interesseRepo;
    private final ProdutoRepository produtoRepository;
    private final ProdutoService produtoService;

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> obterRecomendacoesParaUsuario(Long usuarioId) {
        if (usuarioId == null) {
            return obterFallbackProdutos();
        }

        List<InteresseAdocao> interesses = interesseRepo.findByUsuarioId(usuarioId);
        List<Pet> pets = interesses.stream()
                .map(InteresseAdocao::getPet)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (pets.isEmpty()) {
            return obterFallbackProdutos();
        }

        List<Produto> produtosValidos = produtoRepository.findAll().stream()
                .filter(this::isProdutoElegivel)
                .collect(Collectors.toList());

        Map<Produto, Integer> scores = new HashMap<>();

        for (Produto produto : produtosValidos) {
            int score = calcularScoreParaProduto(produto, pets);
            if (score > 0) {
                scores.put(produto, score);
            }
        }

        List<Produto> recomendados = scores.entrySet().stream()
                .sorted(Map.Entry.<Produto, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(10)
                .collect(Collectors.toList());

        if (recomendados.size() < 4) {
            List<Produto> fallbacks = ordenarPorRelevanciaGeral(produtosValidos).stream()
                    .filter(p -> !recomendados.contains(p))
                    .limit(10 - recomendados.size())
                    .collect(Collectors.toList());
            recomendados.addAll(fallbacks);
        }

        return recomendados.stream()
                .limit(10)
                .map(produtoService::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private int calcularScoreParaProduto(Produto produto, List<Pet> pets) {
        int totalScore = 0;
        String nome = produto.getNome().toLowerCase();
        String descricao = produto.getDescricao() != null ? produto.getDescricao().toLowerCase() : "";

        for (Pet pet : pets) {
            int scorePet = 0;

            if (pet.getEspecie() != null) {
                List<String> especieKeywords = getEspecieKeywords(pet.getEspecie());
                boolean matchEspecie = especieKeywords.stream().anyMatch(kw -> nome.contains(kw) || descricao.contains(kw));
                if (matchEspecie) {
                    scorePet += 10;
                }
            }

            if (pet.getIdade() != null) {
                List<String> idadeKeywords = getIdadeKeywords(pet.getIdade());
                boolean matchIdade = idadeKeywords.stream().anyMatch(kw -> nome.contains(kw) || descricao.contains(kw));
                if (matchIdade) {
                    scorePet += 5;
                }
            }

            if (pet.getPorte() != null) {
                String porteStr = pet.getPorte().name().toLowerCase();
                if (nome.contains(porteStr) || descricao.contains(porteStr)) {
                    scorePet += 3;
                }
            }

            totalScore += scorePet;
        }

        return totalScore;
    }

    private List<String> getEspecieKeywords(Especie especie) {
        switch (especie) {
            case CAO:
                return Arrays.asList("cão", "cao", "cachorro", "canino", "caes", "cães", "puppy", "dog");
            case GATO:
                return Arrays.asList("gato", "gata", "felino", "felina", "gatito", "gatinho", "gatinha", "cat");
            default:
                return Collections.emptyList();
        }
    }

    private List<String> getIdadeKeywords(int idade) {
        if (idade < 1) {
            return Arrays.asList("filhote", "puppy", "filhotes", "crescimento", "junior");
        } else if (idade > 7) {
            return Arrays.asList("senior", "sênior", "idoso", "idosos", "velhice", "maturidade");
        } else {
            return Arrays.asList("adulto", "adultos");
        }
    }

    private List<ProdutoResponseDTO> obterFallbackProdutos() {
        return ordenarPorRelevanciaGeral(produtoRepository.findAll()).stream()
                .filter(this::isProdutoElegivel)
                .limit(10)
                .map(produtoService::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private List<Produto> ordenarPorRelevanciaGeral(List<Produto> produtos) {
        return produtos.stream()
                .filter(this::isProdutoElegivel)
                .sorted(Comparator.comparingDouble(this::calcularNotaMedia)
                        .reversed()
                        .thenComparing(Produto::getDataCriacao, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Produto::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private boolean isProdutoElegivel(Produto produto) {
        return produto != null
                && produto.getStatus() == StatusProduto.ATIVO
                && produto.getPetshop() != null
                && produto.getPetshop().isAprovado();
    }

    private double calcularNotaMedia(Produto produto) {
        if (produto.getAvaliacoes() == null || produto.getAvaliacoes().isEmpty()) {
            return 0.0;
        }

        return produto.getAvaliacoes().stream()
                .mapToInt(AvaliacaoProduto::getNota)
                .average()
                .orElse(0.0);
    }
}
