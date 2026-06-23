package com.Mybuddy.Myb.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade InteresseAdocao adaptada para o MongoDB.
 * Representa a solicitação de intenção de adoção de um pet por parte de um usuário.
 */
@Document(collection = "interesses_adocao")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InteresseAdocao implements Identifiable {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @Indexed
    @DocumentReference(lazy = true)
    @ToString.Exclude
    private Usuario usuario;

    @Indexed
    @DocumentReference(lazy = true)
    @ToString.Exclude
    private Pet pet;

    @Indexed
    private StatusInteresse status;


    private String mensagem;

    private String cpfAdotante;

    private Integer idadeAdotante;

    private String motivoAdocao;

    private String tipoResidencia;

    private Boolean possuiTelasProtecao;

    private String outrosAnimais;

    private Integer tempoSozinhoHoras;

    private Boolean todosCientes;

    private Boolean espacoAdequado;

    @CreatedDate
    private LocalDateTime criadoEm;

    @LastModifiedDate
    private LocalDateTime atualizadoEm;
}