package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.StatusInteresse;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(
        @NotNull StatusInteresse status
) {}