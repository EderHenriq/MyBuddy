package com.Mybuddy.Myb.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Representação de uma avaliação de produto retornada pela API, incluindo o nome do
 * cliente que avaliou para exibição pública.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoProdutoResponseDTO {
    private Long id;
    private Long produtoId;
    private Long clienteId;
    private String clienteNome;
    private Integer nota;
    private String comentario;
    private LocalDateTime dataCriacao;
}
