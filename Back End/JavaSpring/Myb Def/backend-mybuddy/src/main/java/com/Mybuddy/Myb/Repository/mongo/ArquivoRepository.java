package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.Arquivo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório MongoDB para a entidade Arquivo.
 */
@Repository
public interface ArquivoRepository extends MongoRepository<Arquivo, String> {
}
