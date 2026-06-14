package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório MongoDB para a entidade Chat.
 */
@Repository
public interface ChatRepository extends MongoRepository<Chat, Long> {
}
