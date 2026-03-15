package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.StatusAdocao; // Importa o enum StatusAdocao

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

        List<String> fotosUrls, // Lista de URLs das fotos

        String statusAdocaoDescricao, // Descrição traduzida do status (Ex: "Em Adoção")
        StatusAdocao statusAdocao,    // Adicionado o ENUM original do status (importante para edição e lógica do botão)

        String nomeOrganizacao,       // Corrigido de NomeFantasia para nomeOrganizacao para clareza e padronização
        Long organizacaoId,           // Adicionado o ID da organização (importante para edição)

        boolean microchipado,
        boolean vacinado,
        boolean castrado,
        String cidade,
        String estado
) {}