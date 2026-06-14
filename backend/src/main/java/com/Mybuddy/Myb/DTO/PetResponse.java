package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.StatusAdocao;
import java.util.List;

public record PetResponse(
        Long id,
        String nome,
        String especie,
        String raca,
        Integer idade,
        String porte,
        String cor,
        String pelagem,
        String sexo,
        List<String> fotosUrls,
        String statusAdocaoDescricao,
        StatusAdocao statusAdocao,
        String nomeOrganizacao,
        Long organizacaoId,
        boolean microchipado,
        boolean vacinado,
        boolean castrado,
        String cidade,
        String estado,
        String descricao
) {}