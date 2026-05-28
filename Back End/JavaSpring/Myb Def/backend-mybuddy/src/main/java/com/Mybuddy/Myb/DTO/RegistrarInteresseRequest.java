package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarInteresseRequest(
        @NotNull(message = "O ID do pet é obrigatório")
        Long petId,

        @Size(max = 500, message = "A mensagem não pode exceder 500 caracteres")
        String mensagem
) {}
