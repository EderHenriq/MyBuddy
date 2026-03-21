package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.Organizacao;

public class OrganizacaoResponseDTO {
    private Long id;
    private String nomeFantasia;
    private String emailContato;
    private String cnpj;
    private String telefoneContato;
    private String endereco;
    private String descricao;
    private String website;
    // Opcional: Adicionar contagem de pets, usuários, etc.
    // private int numeroDePets;

    // Construtor a partir da Entidade
    public OrganizacaoResponseDTO(Organizacao organizacao) {
        this.id = organizacao.getId();
        this.nomeFantasia = organizacao.getNomeFantasia();
        this.emailContato = organizacao.getEmailContato();
        this.cnpj = organizacao.getCnpj();
        this.telefoneContato = organizacao.getTelefoneContato();
        this.endereco = organizacao.getEndereco();
        this.descricao = organizacao.getDescricao();
        this.website = organizacao.getWebsite();
        // this.numeroDePets = organizacao.getPets().size();
    }

    // Construtor vazio
    public OrganizacaoResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    public String getEmailContato() { return emailContato; }
    public void setEmailContato(String emailContato) { this.emailContato = emailContato; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getTelefoneContato() { return telefoneContato; }
    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}