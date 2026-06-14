package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.AvaliacaoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoProdutoRepository extends JpaRepository<AvaliacaoProduto, Long> {
    List<AvaliacaoProduto> findByProdutoId(Long produtoId);
}
