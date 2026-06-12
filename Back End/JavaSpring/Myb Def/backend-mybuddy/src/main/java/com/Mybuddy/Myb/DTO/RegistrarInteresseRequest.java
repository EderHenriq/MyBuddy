package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarInteresseRequest(
        @NotNull(message = "O ID do pet é obrigatório")
        Long petId,

        @Size(max = 500, message = "A mensagem não pode exceder 500 caracteres")
        String mensagem,

        @NotBlank(message = "O CPF do adotante é obrigatório.")
        @Size(min = 11, max = 14, message = "CPF inválido.")
        String cpfAdotante,

        @NotNull(message = "A idade do adotante é obrigatória.")
        @Min(value = 18, message = "Você deve ter pelo menos 18 anos para adotar.")
        Integer idadeAdotante,

        @NotBlank(message = "O motivo da adoção é obrigatório.")
        @Size(max = 500, message = "O motivo não pode exceder 500 caracteres.")
        String motivoAdocao,

        @NotBlank(message = "O tipo de residência é obrigatório.")
        String tipoResidencia,

        @NotNull(message = "A informação sobre telas de proteção é obrigatória.")
        Boolean possuiTelasProtecao,

        @NotBlank(message = "A informação sobre outros animais é obrigatória.")
        String outrosAnimais,

        @NotNull(message = "O tempo que o animal ficará sozinho é obrigatório.")
        @Min(value = 0, message = "O tempo sozinho não pode ser negativo.")
        @Max(value = 24, message = "O tempo sozinho não pode exceder 24 horas.")
        Integer tempoSozinhoHoras,

        @NotNull(message = "A confirmação de que todos na residência estão cientes é obrigatória.")
        Boolean todosCientes,

        @NotNull(message = "A confirmação de espaço adequado é obrigatória.")
        Boolean espacoAdequado
) {}
