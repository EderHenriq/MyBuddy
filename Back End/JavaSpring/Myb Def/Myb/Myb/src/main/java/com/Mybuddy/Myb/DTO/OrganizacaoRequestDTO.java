package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CNPJ;

public class OrganizacaoRequestDTO {

    @NotBlank(message = "O nome fantasia é obrigatório.")
    private String nomeFantasia;

    @NotBlank(message = "O e-mail de contato é obrigatório.")
    @Email(message = "E-mail de contato inválido.")
    private String emailContato;

    @CNPJ(message = "CNPJ inválido.") // Validação específica para CNPJ brasileiro
    @NotBlank(message = "O CNPJ é obrigatório.")
    private String cnpj;

    @NotBlank(message = "O telefone de contato é obrigatório.")
    @Pattern(regexp = "^\\([1-9]{2}\\) [9]{0,1}[0-9]{4}\\-[0-9]{4}$", message = "Telefone inválido. Formato esperado: (XX) XXXXX-XXXX ou (XX) XXXX-XXXX")
    private String telefoneContato;

    @NotBlank(message = "O endereço é obrigatório.")
    private String endereco;

    private String descricao; // Pode ser null ou vazio, então não @NotBlank

    @Pattern(regexp = "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*\\/?$", message = "URL do site inválida.")
    private String website; // Pode ser null ou vazio

    // Construtores (se não estiver usando Lombok)
    public OrganizacaoRequestDTO() {}

    public OrganizacaoRequestDTO(String nomeFantasia, String emailContato, String cnpj, String telefoneContato, String endereco, String descricao, String website) {
        this.nomeFantasia = nomeFantasia;
        this.emailContato = emailContato;
        this.cnpj = cnpj;
        this.telefoneContato = telefoneContato;
        this.endereco = endereco;
        this.descricao = descricao;
        this.website = website;
    }

    // Getters e Setters (se não estiver usando Lombok)
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