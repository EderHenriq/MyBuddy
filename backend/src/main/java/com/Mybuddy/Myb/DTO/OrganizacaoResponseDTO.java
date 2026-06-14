package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.Organizacao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizacaoResponseDTO {

    private Long id;
    private String nomeFantasia;
    private String emailContato;
    private String cnpj;
    private String telefoneContato;
    private String endereco;
    private String descricao;
    private String website;

    // Construtor de mapeamento mantido — contém lógica de conversão da entidade
    public OrganizacaoResponseDTO(Organizacao organizacao) {
        this.id = organizacao.getId();
        this.nomeFantasia = organizacao.getNomeFantasia();
        this.emailContato = organizacao.getEmailContato();
        this.cnpj = organizacao.getCnpj();
        this.telefoneContato = organizacao.getTelefoneContato();
        this.endereco = organizacao.getEndereco();
        this.descricao = organizacao.getDescricao();
        this.website = organizacao.getWebsite();
    }
}