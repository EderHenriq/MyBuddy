package com.Mybuddy.Myb.Security;

// Este é um enum simples que define os tipos de papéis no sistema.
// Ele não precisa de anotações JPA.
public enum ERole {
    ROLE_ADOTANTE, // Para usuários que buscam adotar pets
    ROLE_ONG,      // Para usuários que gerenciam uma organização
    ROLE_ADMIN     // Para administradores do sistema
}