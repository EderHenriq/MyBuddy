package com.Mybuddy.Myb.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record DadosUsuarioExportDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        LocalDateTime dataCriacao,
        List<InteresseExportDTO> interessesAdocao,
        List<PedidoExportDTO> pedidos
) {
    public record InteresseExportDTO(
            Long id,
            Long petId,
            String statusInteresse,
            LocalDateTime criadoEm
    ) {}

    public record PedidoExportDTO(
            Long id,
            String statusPedido,
            LocalDateTime criadoEm
    ) {}
}
