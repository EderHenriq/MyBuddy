package com.Mybuddy.Myb.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
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
public class EventoOng implements Identifiable {

    @Id
    private Long id;

    private String nome;

    private String local;

    private String data;

    private String status;

    @Indexed
    @DocumentReference(lazy = true)
    private Organizacao organizacao;

    public EventoOng(Long id, String nome, String local, String data, String status) {
        this.id = id;
        this.nome = nome;
        this.local = local;
        this.data = data;
        this.status = status;
    }
}
