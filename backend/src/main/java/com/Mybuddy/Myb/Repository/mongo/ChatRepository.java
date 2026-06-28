package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositório MongoDB para a entidade Chat.
 */
public interface ChatRepository extends MongoRepository<Chat, Long> {
}
