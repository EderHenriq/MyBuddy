package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoEntregaDTO {

    @NotBlank(message = "O CEP é obrigatório.")
    @Size(max = 20, message = "O CEP deve ter no máximo 20 caracteres.")
    private String cep;

    @NotBlank(message = "O logradouro é obrigatório.")
    @Size(max = 255, message = "O logradouro deve ter no máximo 255 caracteres.")
    private String logradouro;

    @NotBlank(message = "O número é obrigatório.")
    @Size(max = 50, message = "O número deve ter no máximo 50 caracteres.")
    private String numero;

    @Size(max = 255, message = "O complemento deve ter no máximo 255 caracteres.")
    private String complemento;

    @NotBlank(message = "O bairro é obrigatório.")
    @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres.")
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória.")
    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório.")
    @Size(max = 50, message = "O estado deve ter no máximo 50 caracteres.")
    private String estado;

    private Double latitude;
    private Double longitude;

    // Construtor de compatibilidade com os testes antigos
    public EnderecoEntregaDTO(String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String estado) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }
}
