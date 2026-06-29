package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.AvaliacaoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoProdutoRepository extends JpaRepository<AvaliacaoProduto, Long> {
    List<AvaliacaoProduto> findByProdutoId(Long produtoId);
}
