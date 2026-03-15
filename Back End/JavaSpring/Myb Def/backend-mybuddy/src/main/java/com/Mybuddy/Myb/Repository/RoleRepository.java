package com.Mybuddy.Myb.Repository;
// Define o pacote onde essa interface está localizada, organizado dentro do projeto Mybuddy

import com.Mybuddy.Myb.Security.Role; // Importa a classe Role, que representa a entidade de perfil/role do usuário
import com.Mybuddy.Myb.Security.ERole; // Importa o enum ERole, que define os tipos possíveis de roles (ex: ADMIN, USER)
import org.springframework.data.jpa.repository.JpaRepository; // Importa JpaRepository do Spring Data, que fornece métodos prontos de CRUD
import org.springframework.stereotype.Repository; // Importa a anotação @Repository do Spring

import java.util.Optional; // Importa Optional, usado para lidar com valores que podem ou não existir

@Repository
// Indica que essa interface é um repositório Spring, responsável por operações de persistência no banco de dados
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Extende JpaRepository, passando a entidade Role e o tipo do ID (Integer)
    // Isso já fornece métodos prontos como save(), findById(), findAll(), delete(), etc.

    Optional<Role> findByName(ERole name);
    // Método personalizado para buscar um Role pelo nome
    // Retorna Optional<Role>, pois pode não encontrar nenhum registro com esse nome
    // O Spring Data JPA implementa automaticamente esse método baseado na convenção de nomes
}
