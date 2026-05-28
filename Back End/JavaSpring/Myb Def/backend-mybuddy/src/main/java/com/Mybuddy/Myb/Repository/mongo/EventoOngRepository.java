package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.EventoOng;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório MongoDB para a entidade EventoOng.
 */
@Repository
public interface EventoOngRepository extends MongoRepository<EventoOng, Long> {
}
