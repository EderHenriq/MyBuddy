package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto é obrigatório.")
    @Size(max = 150, message = "O nome do produto deve ter no máximo 150 caracteres.")
    private String nome;

    private String descricao;

    @NotNull(message = "O preço do produto é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço do produto deve ser maior ou igual a 0.01.")
    private BigDecimal preco;

    @NotNull(message = "A quantidade em estoque é obrigatória.")
    @Min(value = 0, message = "A quantidade em estoque deve ser maior ou igual a 0.")
    private Integer estoque;

    @NotNull(message = "O ID da subcategoria é obrigatório.")
    private Long subCategoriaId;

    private List<String> imagens;
}
