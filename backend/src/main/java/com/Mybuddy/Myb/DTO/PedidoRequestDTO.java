package com.Mybuddy.Myb.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    @NotNull(message = "O ID do petshop é obrigatório.")
    private Long petshopId;

    @NotNull(message = "O endereço de entrega é obrigatório.")
    @Valid
    private EnderecoEntregaDTO enderecoEntrega;

    @NotEmpty(message = "O pedido deve conter pelo menos um item.")
    @Valid
    private List<ItemPedidoRequestDTO> itens;

    private String cupomDesconto;
}
