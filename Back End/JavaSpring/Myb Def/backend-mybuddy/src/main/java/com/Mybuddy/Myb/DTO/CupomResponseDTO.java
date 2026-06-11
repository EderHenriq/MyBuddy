package com.Mybuddy.Myb.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
}
