package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.FotoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FotoProdutoRepository extends JpaRepository<FotoProduto, Long> {
    void deleteByProdutoId(Long produtoId);
}
