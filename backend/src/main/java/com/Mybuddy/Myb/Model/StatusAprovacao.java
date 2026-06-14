package com.Mybuddy.Myb.Model;

/**
 * Status de aprovação de ONGs e Petshops na plataforma.
 *
 * Fluxo padrão:
 *   PENDENTE_APROVACAO → APROVADO
 *   PENDENTE_APROVACAO → REJEITADO
 *
 * Apenas entidades com status APROVADO podem:
 *  - Para Petshops: cadastrar e listar produtos publicamente
 *  - Para ONGs: criar campanhas de doação e listar pets publicamente
 */
public enum StatusAprovacao {
    /** Entidade acabou de se cadastrar e aguarda análise de um administrador. */
    PENDENTE_APROVACAO,

    /** Entidade foi verificada e aprovada para operar na plataforma. */
    APROVADO,

    /** Entidade teve seu cadastro negado (CNPJ inválido, documentação incompleta, fraude, etc.). */
    REJEITADO
}
