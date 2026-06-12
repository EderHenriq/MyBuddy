package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

@Getter
@Setter
@NoArgsConstructor
public class OrganizacaoRequestDTO {

    @NotBlank(message = "O nome fantasia é obrigatório.")
    private String nomeFantasia;

    @NotBlank(message = "O e-mail de contato é obrigatório.")
    @Email(message = "E-mail de contato inválido.")
    private String emailContato;

    @CNPJ(message = "CNPJ inválido.")
    @NotBlank(message = "O CNPJ é obrigatório.")
    private String cnpj;

    @NotBlank(message = "O telefone de contato é obrigatório.")
    @Pattern(regexp = "^\\([1-9]{2}\\) [9]{0,1}[0-9]{4}\\-[0-9]{4}$",
            message = "Telefone inválido. Formato esperado: (XX) XXXXX-XXXX ou (XX) XXXX-XXXX")
    private String telefoneContato;

    @NotBlank(message = "O endereço é obrigatório.")
    private String endereco;

    private String descricao;

    @Pattern(regexp = "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*\\/?$",
            message = "URL do site inválida.")
    private String website;
}