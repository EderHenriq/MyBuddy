package com.Mybuddy.Myb.Model; // Declara o pacote onde esta enumeração (enum) está localizada.

// Declara uma enumeração chamada StatusAdocao.
// Uma enumeração é um tipo de dado especial que permite que uma variável seja um conjunto de constantes pré-definidas.
// Ela é usada aqui para representar os diferentes estados em que um pet pode estar em relação ao processo de adoção.
public enum StatusAdocao {
    ADOTADO,   // Constante que indica que o pet já foi adotado e não está mais disponível.
    RESERVADO, // Constante que indica que o pet foi reservado, mas ainda não foi formalmente adotado.
    INDISPONIVEL,// Constante que indica que o pet não está disponível para adoção no momento (por qualquer motivo).
    DISPONIVEL // Constante que indica que o pet está atualmente disponível para ser adotado.
}