package com.Mybuddy.Myb.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

/**
 * Entidade para armazenar as sequências auto-incrementais no MongoDB.
 */
@Document(collection = "database_sequences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseSequence {

    @Id
    private String id;

    private long seq;
}
