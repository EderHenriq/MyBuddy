package com.Mybuddy.Myb.Security; // Declara o pacote onde esta enumeração de papéis (roles) está localizada.

// Declara uma enumeração chamada ERole.
// Uma enumeração é um tipo de dado especial que permite que uma variável seja um conjunto de constantes pré-definidas.
// Ela é usada aqui para definir os diferentes papéis (roles) que um usuário pode ter no sistema,
// seguindo a convenção do Spring Security de prefixar roles com "ROLE_".
public enum ERole {
    ROLE_ADOTANTE, // Constante que representa o papel de um usuário que busca pets para adoção.
    ROLE_ONG,      // Constante que representa o papel de uma organização não governamental (ONG) que cuida de pets.
    ROLE_ADMIN     // Constante que representa o papel de um administrador do sistema, com privilégios elevados.
}