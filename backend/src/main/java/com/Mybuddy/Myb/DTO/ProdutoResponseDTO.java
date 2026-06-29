package com.Mybuddy.Myb.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer estoque;
    private String status;
    private Long subCategoriaId;
    private String subCategoriaNome;
    private Long categoriaId;
    private String categoriaNome;
    private Long petshopId;
    private String petshopNome;
    private List<String> imagens;
    private Double notaMedia;

    private String marca;
    private String origem;
    private String porteRaca;
    private String peso;
    private String idade;
}
