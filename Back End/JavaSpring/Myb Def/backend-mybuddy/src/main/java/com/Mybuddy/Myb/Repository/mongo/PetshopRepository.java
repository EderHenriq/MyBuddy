package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.Petshop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório do MongoDB para a entidade Petshop.
 * Todos os logs e comentários seguem os padrões em pt-br.
 */
@Repository
public interface PetshopRepository extends MongoRepository<Petshop, Long> {

    /**
     * Busca um petshop através do seu CNPJ.
     *
     * @param cnpj O CNPJ do petshop.
     * @return Um Optional contendo o Petshop se encontrado.
     */
    Optional<Petshop> findByCnpj(String cnpj);
}
