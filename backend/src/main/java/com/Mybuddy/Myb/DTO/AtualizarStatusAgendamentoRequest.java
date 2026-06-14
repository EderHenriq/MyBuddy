package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.StatusAgendamento;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusAgendamentoRequest(
        @NotNull StatusAgendamento status
) {}
