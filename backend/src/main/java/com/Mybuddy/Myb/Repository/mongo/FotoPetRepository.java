package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.FotoPet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório MongoDB para a entidade FotoPet.
 */
@Repository
public interface FotoPetRepository extends MongoRepository<FotoPet, Long> {
}
