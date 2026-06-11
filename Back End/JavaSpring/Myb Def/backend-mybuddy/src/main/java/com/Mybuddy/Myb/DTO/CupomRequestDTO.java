package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de criação/atualização de cupom.
 * Inclui os campos de prevenção de abuso: validade, valor mínimo e limites de uso.
 */
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

    /** Data de início da validade do cupom (inclusive). Null = sem restrição de início. */
    private LocalDate dataInicio;

    /** Data de expiração do cupom (inclusive). Null = sem expiração. */
    private LocalDate dataExpiracao;

    /** Valor mínimo do pedido para o cupom ser aplicável. Null = sem valor mínimo. */
    @DecimalMin(value = "0.00", message = "O valor mínimo do pedido não pode ser negativo.")
    private BigDecimal valorMinimoPedido;

    /** Limite total de usos do cupom na plataforma. Null = ilimitado. */
    @Min(value = 1, message = "O limite de uso geral deve ser no mínimo 1.")
    private Integer limiteUsoGeral;
}
