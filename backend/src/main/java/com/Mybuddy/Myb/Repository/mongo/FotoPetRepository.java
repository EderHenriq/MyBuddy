package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.FotoPet;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositório MongoDB para a entidade FotoPet.
 */
public interface FotoPetRepository extends MongoRepository<FotoPet, Long> {
}
