package com.Mybuddy.Myb.Dto;

import com.Mybuddy.Myb.Model.StatusInteresse;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(
        @NotNull StatusInteresse status
) {}