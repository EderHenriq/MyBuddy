package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.EventoOng;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositório MongoDB para a entidade EventoOng.
 */
public interface EventoOngRepository extends MongoRepository<EventoOng, Long> {
}
