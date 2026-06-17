package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.Arquivo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositório MongoDB para a entidade Arquivo.
 */
public interface ArquivoRepository extends MongoRepository<Arquivo, String> {
}
