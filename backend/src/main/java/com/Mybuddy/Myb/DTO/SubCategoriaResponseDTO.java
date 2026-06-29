package com.Mybuddy.Myb.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoriaResponseDTO {
    private Long id;
    private String nome;
    private Long categoriaId;
}
