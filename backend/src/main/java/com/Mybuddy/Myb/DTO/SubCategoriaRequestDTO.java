package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoriaRequestDTO {

    @NotBlank(message = "O nome da subcategoria é obrigatório.")
    @Size(max = 100, message = "O nome da subcategoria deve ter no máximo 100 caracteres.")
    private String nome;

    @NotNull(message = "O ID da categoria pai é obrigatório.")
    private Long categoriaId;
}
