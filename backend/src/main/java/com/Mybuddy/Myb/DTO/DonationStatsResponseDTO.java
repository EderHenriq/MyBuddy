package com.Mybuddy.Myb.DTO;

import java.math.BigDecimal;

public record DonationStatsResponseDTO(
    long petsSalvos,
    long ongsParceiras,
    BigDecimal totalArrecadado,
    long doadoresAtivos
) {}
