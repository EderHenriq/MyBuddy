package com.Mybuddy.Myb.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarInteresseRequest(
        @NotNull Long usuarioId,
        @NotNull Long petId,
        @Size(max = 500) String mensagem
) {}

