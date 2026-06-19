package com.Mybuddy.Myb.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

/**
 * Entidade Organizacao (ONG) adaptada para o MongoDB.
 * Representa as ONGs de resgate e adoção de animais.
 */
@Document(collection = "organizacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Organizacao implements Identifiable {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String nomeFantasia;

    private String emailContato;

    private String cnpj;

    private String telefoneContato;

    private String endereco;

    private String descricao;

    private String website;

    private Double latitude;
    private Double longitude;

    /**
     * Status de aprovação da ONG na plataforma.
     * Apenas ONGs APROVADAS podem criar campanhas de doação e listar pets publicamente.
     * O CNPJ é coletado no cadastro e validado pelo administrador antes da aprovação.
     */
    @Builder.Default
    private StatusAprovacao statusAprovacao = StatusAprovacao.PENDENTE_APROVACAO;

    /** Verifica se a ONG está aprovada para operar. */
    public boolean isAprovada() {
        return StatusAprovacao.APROVADO == this.statusAprovacao;
    }

    /** Verifica se a ONG ainda aguarda aprovação. */
    public boolean isPendente() {
        return StatusAprovacao.PENDENTE_APROVACAO == this.statusAprovacao;
    }

}
