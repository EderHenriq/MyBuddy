package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetshopRequestDTO {

    @NotBlank(message = "O nome fantasia é obrigatório.")
    @Size(max = 150, message = "O nome fantasia deve ter no máximo 150 caracteres.")
    private String nomeFantasia;

    @Email(message = "O e-mail de contato deve ser válido.")
    @Size(max = 100, message = "O e-mail de contato deve ter no máximo 100 caracteres.")
    private String emailContato;

    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos numéricos (apenas números).")
    private String cnpj;

    @Size(max = 20, message = "O telefone de contato deve ter no máximo 20 caracteres.")
    private String telefoneContato;

    @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres.")
    private String endereco;

    private String descricao;

    @Size(max = 100, message = "O website deve ter no máximo 100 caracteres.")
    private String website;
}
