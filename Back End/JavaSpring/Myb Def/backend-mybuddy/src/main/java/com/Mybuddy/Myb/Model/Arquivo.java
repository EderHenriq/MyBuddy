package com.Mybuddy.Myb.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

/**
 * Entidade que armazena os metadados e os dados binários de arquivos/fotos no MongoDB.
 */
@Document(collection = "arquivos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arquivo {

    @Id
    private String id; // Nome gerado (geralmente UUID + extensão)

    private String nomeOriginal;

    private String tipoConteudo;

    private byte[] dados;
}
