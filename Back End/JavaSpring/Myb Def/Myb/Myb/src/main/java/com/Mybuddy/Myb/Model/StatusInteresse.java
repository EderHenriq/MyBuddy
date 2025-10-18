package com.Mybuddy.Myb.Model; // Declara o pacote onde esta enumeração (enum) está localizada.

// Declara uma enumeração chamada StatusInteresse.
// Esta enumeração é usada para definir os possíveis estados de um "interesse de adoção"
// manifestado por um usuário em relação a um pet.
public enum StatusInteresse {
    PENDENTE, // Constante que indica que o interesse de adoção foi registrado e está aguardando avaliação/decisão.
    APROVADO, // Constante que indica que o interesse de adoção foi aceito.
    REJEITADO // Constante que indica que o interesse de adoção não foi aceito.
}