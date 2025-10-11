package com.Mybuddy.Myb.Dto;

import com.Mybuddy.Myb.Model.StatusInteresse;

import java.time.LocalDateTime;

public record InteresseResponse(
        Long id,
        Long usuarioId,
        Long petId,
        StatusInteresse status,
        String mensagem,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}
