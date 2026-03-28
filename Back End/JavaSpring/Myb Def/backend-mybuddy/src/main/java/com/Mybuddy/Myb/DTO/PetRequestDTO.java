package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.Especie;
import com.Mybuddy.Myb.Model.Porte;
import com.Mybuddy.Myb.Model.StatusAdocao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PetRequestDTO {

    @NotBlank(message = "O nome do pet é obrigatório.")
    @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres.")
    private String nome;

    @NotNull(message = "A espécie do pet é obrigatória.")
    private Especie especie;

    @NotBlank(message = "A raça do pet é obrigatória.")
    @Size(max = 60, message = "A raça deve ter no máximo 60 caracteres.")
    private String raca;

    @NotNull(message = "A idade do pet é obrigatória.")
    @Min(value = 0, message = "A idade não pode ser negativa.")
    private Integer idade;

    @NotBlank(message = "A cor do pet é obrigatória.")
    @Size(max = 30, message = "A cor deve ter no máximo 30 caracteres.")
    private String cor;

    @NotNull(message = "O porte do pet é obrigatório.")
    private Porte porte;

    @NotBlank(message = "O sexo do pet é obrigatório.")
    @Size(max = 10, message = "O sexo deve ter no máximo 10 caracteres.")
    private String sexo;

    @Size(max = 3, message = "São permitidas no máximo 3 fotos por pet.")
    private List<String> fotosUrls;

    @NotNull(message = "O status de adoção é obrigatório.")
    private StatusAdocao statusAdocao;

    @NotNull(message = "O ID da organização é obrigatório.")
    private Long organizacaoId;

    @NotNull(message = "A informação de microchip é obrigatória.")
    private boolean microchipado;

    @NotNull(message = "A informação de vacinação é obrigatória.")
    private boolean vacinado;

    @NotNull(message = "A informação de castração é obrigatória.")
    private boolean castrado;

    @Size(max = 60, message = "A pelagem deve ter no máximo 60 caracteres.")
    private String pelagem;

    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
    private String cidade;

    @Size(max = 100, message = "O estado deve ter no máximo 100 caracteres.")
    private String estado;
}