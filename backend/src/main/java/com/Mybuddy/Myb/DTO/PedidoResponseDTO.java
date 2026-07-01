package com.Mybuddy.Myb.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponseDTO {
    private Long id;
    private Long clienteId;
    private String clienteNome;
    private Long petshopId;
    private String petshopNome;
    private EnderecoEntregaDTO enderecoEntrega;
    private List<ItemPedidoResponseDTO> itens;
    private BigDecimal valorTotal;
    private BigDecimal valorFrete;
    private String cupomDesconto;
    private BigDecimal valorDesconto;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
