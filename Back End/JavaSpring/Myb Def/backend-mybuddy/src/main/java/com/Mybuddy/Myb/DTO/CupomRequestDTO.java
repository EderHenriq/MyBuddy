package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CupomRequestDTO {

    @NotBlank(message = "O código do cupom é obrigatório.")
    @Size(max = 100, message = "O código do cupom deve ter no máximo 100 caracteres.")
    private String codigo;

    @NotNull(message = "O percentual de desconto é obrigatório.")
    @DecimalMin(value = "0.00", message = "O desconto não pode ser negativo.")
    @DecimalMax(value = "100.00", message = "O desconto não pode ser maior que 100%.")
    private BigDecimal percentualDesconto;

    private Long petshopId;

    private Boolean ativo;
}
