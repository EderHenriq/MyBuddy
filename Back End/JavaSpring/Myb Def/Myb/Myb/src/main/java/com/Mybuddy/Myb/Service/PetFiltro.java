package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.StatusAdocao; // Importar o enum StatusAdocao

public record PetFiltro(
        String nome,
        String especie,
        String porte,
        String sexo,
        Integer idadeMin,
        Integer idadeMax,
        StatusAdocao statusAdocao // Adicionado o campo statusAdocao
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