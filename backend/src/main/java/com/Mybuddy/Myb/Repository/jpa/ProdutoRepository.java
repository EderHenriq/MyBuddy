package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Repositório JPA para a entidade Produto (PostgreSQL).
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    /**
     * Busca os produtos pelo ID numérico do petshop associado.
     */
    List<Produto> findByPetshopId(Long petshopId);
}
