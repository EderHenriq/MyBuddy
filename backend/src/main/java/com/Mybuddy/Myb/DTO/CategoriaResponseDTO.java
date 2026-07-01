package com.Mybuddy.Myb.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Representação de uma categoria de produtos retornada pela API, incluindo suas subcategorias.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponseDTO {
    private Long id;
    private String nome;
    private List<SubCategoriaResponseDTO> subcategorias;
}
