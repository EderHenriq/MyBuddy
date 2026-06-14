package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoRequestDTO {

    @NotBlank(message = "O nome do serviço é obrigatório.")
    @Size(max = 255, message = "O nome do serviço deve ter no máximo 255 caracteres.")
    private String nome;

    private String descricao;

    @NotNull(message = "O preço do serviço é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço do serviço deve ser maior que zero.")
    private BigDecimal preco;

    @NotNull(message = "A duração do serviço é obrigatória.")
    @Min(value = 1, message = "A duração do serviço deve ser maior que zero.")
    private Integer duracaoMinutos;

    private Boolean ativo;
}
