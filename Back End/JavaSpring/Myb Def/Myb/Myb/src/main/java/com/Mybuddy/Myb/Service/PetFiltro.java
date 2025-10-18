package com.Mybuddy.Myb.Service;
// Pacote onde a classe/record PetFiltro está localizada, provavelmente usado no serviço de pets

public record PetFiltro(
        String nome,      // Nome do pet para filtro (pode ser parcial ou completo)
        String especie,   // Espécie do pet (ex: cachorro, gato) para filtro
        String porte,     // Porte do pet (ex: pequeno, médio, grande) para filtro
        String sexo,      // Sexo do pet (ex: macho, fêmea) para filtro
        Integer idadeMin, // Idade mínima do pet para filtro
        Integer idadeMax  // Idade máxima do pet para filtro
) {
    // Record em Java é uma classe imutável e compacta, ideal para transportar dados
    // Ele já gera automaticamente:
    // - Construtor
    // - Getters (nomes iguais aos campos)
    // - equals(), hashCode() e toString()
}
