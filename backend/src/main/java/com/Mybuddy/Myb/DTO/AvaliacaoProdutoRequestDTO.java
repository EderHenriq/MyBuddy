package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoProdutoRequestDTO {

    @NotNull(message = "A nota é obrigatória.")
    @Min(value = 1, message = "A nota mínima é 1.")
    @Max(value = 5, message = "A nota máxima é 5.")
    private Integer nota;

    @Size(max = 1000, message = "O comentário deve ter no máximo 1000 caracteres.")
    private String comentario;
}
