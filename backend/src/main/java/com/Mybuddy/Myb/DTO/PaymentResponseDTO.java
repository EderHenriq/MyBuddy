package com.Mybuddy.Myb.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.Mybuddy.Myb.Model.PaymentStatus;

public record PaymentResponseDTO(
    Long id,
    String mpPreferenceId,
    String mpPaymentId,
    Long usuarioId,
    Long petId,
    Long campanhaId,
    Long organizacaoId,
    BigDecimal amount,
    PaymentStatus status,
    String initPoint,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
){}
