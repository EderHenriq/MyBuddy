package com.Mybuddy.Myb.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PaymentRequestDTO(

    Long petId,

    @NotNull(message = "amount é obrigatório")
    @Positive(message = "amount deve ser positivo")
    BigDecimal amount,

    String description
) {}