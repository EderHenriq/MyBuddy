package com.Mybuddy.Myb.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

/**
 * Entidade EventoOng adaptada para o MongoDB.
 * Representa os eventos organizados pelas ONGs parceiras.
 */
@Document(collection = "eventos_ong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoOng {

    @Id
    private Long id;

    private String nome;

    private String local;

    private String data;

    private String status;
}
