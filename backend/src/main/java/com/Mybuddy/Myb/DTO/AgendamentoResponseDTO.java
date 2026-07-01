package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.StatusAgendamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representação de um agendamento de serviço retornada pela API, com dados do serviço,
 * petshop, horário e status para exibição ao usuário.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendamentoResponseDTO {
    private Long id;
    private Long clienteId;
    private Long petId;
    private Long servicoId;
    private String servicoNome;
    private Long petshopId;
    private String petshopNome;
    private BigDecimal preco;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private StatusAgendamento status;
    private String profissionalNome;
}
