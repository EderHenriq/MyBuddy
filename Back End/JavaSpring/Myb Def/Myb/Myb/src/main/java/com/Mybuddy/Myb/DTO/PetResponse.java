package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.StatusAdocao;

public record PetResponse(
        Long id,
        String nome,
        String especie,
        String raca,
        Integer idade,
        String porte,
        String cor,
        String sexo,
        String imageUrl,
        StatusAdocao statusAdocao,
        String nomeOrganizacao
) {}

