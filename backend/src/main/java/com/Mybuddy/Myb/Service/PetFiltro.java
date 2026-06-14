package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Especie;
import com.Mybuddy.Myb.Model.Porte;
import com.Mybuddy.Myb.Model.StatusAdocao;

public record PetFiltro(
        String nome,
        Especie especie,
        Porte porte,
        String sexo,
        Integer idadeMin,
        Integer idadeMax,
        StatusAdocao statusAdocao
) {
    // Construtor compacto para definir valores padrão
    // Isso é útil se o filtro não for fornecido e você quiser um padrão no backend.
    public PetFiltro {
        // Se statusAdocao for nulo (não fornecido na requisição), define como DISPONIVEL
        if (statusAdocao == null) {
            statusAdocao = StatusAdocao.DISPONIVEL;
        }
    }
}