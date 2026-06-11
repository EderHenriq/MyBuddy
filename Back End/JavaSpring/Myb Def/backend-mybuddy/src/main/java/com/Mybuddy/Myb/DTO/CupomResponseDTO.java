package com.Mybuddy.Myb.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de resposta de cupom.
 * Expõe todos os campos relevantes para o cliente, incluindo dados de validade e uso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CupomResponseDTO {
    private Long id;
    private String codigo;
    private BigDecimal percentualDesconto;
    private Long petshopId;
    private String petshopNome;
    private boolean ativo;
    private LocalDate dataInicio;
    private LocalDate dataExpiracao;
    private BigDecimal valorMinimoPedido;
    private Integer limiteUsoGeral;
    private int usoAtual;
}
