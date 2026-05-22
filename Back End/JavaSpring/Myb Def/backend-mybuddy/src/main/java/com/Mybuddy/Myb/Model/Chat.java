package com.Mybuddy.Myb.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

/**
 * Entidade Chat adaptada para o MongoDB.
 * Representa os chats de suporte e conversas da plataforma.
 */
@Document(collection = "chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {

    @Id
    private Long id;

    private String cliente;

    private String ultimaMensagem;

    private String horario;

    private String status;
}
