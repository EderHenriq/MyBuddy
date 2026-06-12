package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.StatusInteresse;
import java.time.LocalDateTime;

public record InteresseResponse(
        Long id,
        UsuarioResponse usuario,
        PetResumoResponse pet,
        StatusInteresse status,
        String mensagem,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        String cpfAdotante,
        Integer idadeAdotante,
        String motivoAdocao,
        String tipoResidencia,
        Boolean possuiTelasProtecao,
        String outrosAnimais,
        Integer tempoSozinhoHoras,
        Boolean todosCientes,
        Boolean espacoAdequado
) {}
