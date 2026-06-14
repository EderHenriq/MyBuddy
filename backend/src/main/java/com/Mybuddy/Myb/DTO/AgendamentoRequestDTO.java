package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoRequestDTO {

    @NotNull(message = "O pet é obrigatório.")
    private Long petId;

    @NotNull(message = "O serviço é obrigatório.")
    private Long servicoId;

    @NotNull(message = "A data e hora de início são obrigatórias.")
    @Future(message = "A data e hora de início devem estar no futuro.")
    private LocalDateTime dataHoraInicio;

    @Size(max = 255, message = "O nome do profissional deve ter no máximo 255 caracteres.")
    private String profissionalNome;
}
