package com.Mybuddy.Myb.Repository;
// Define o pacote onde essa interface está localizada dentro do projeto Mybuddy

import com.Mybuddy.Myb.Model.Usuario; // Importa a classe Usuario, que representa a entidade de usuário do sistema
import org.springframework.data.jpa.repository.JpaRepository; // Importa JpaRepository do Spring Data, que fornece métodos prontos de CRUD
import org.springframework.stereotype.Repository; // Importa a anotação @Repository do Spring

import java.util.Optional; // Importa Optional, que é usado para lidar com valores que podem ou não existir no banco de dados

@Repository
// Indica que essa interface é um repositório Spring, responsável por operações de persistência (salvar, buscar, deletar, etc.)
public interface UserRepository extends JpaRepository<Usuario, Long> {
    // Extende JpaRepository, passando a entidade Usuario e o tipo do ID (Long)
    // Isso já fornece métodos prontos como save(), findById(), findAll(), delete(), etc.

    Optional<Usuario> findByEmail(String email);
    // Método personalizado para buscar um usuário pelo email
    // Retorna Optional<Usuario> porque pode não encontrar nenhum usuário com esse email
    // O Spring Data JPA implementa automaticamente esse método baseado na convenção de nomes

    Boolean existsByEmail(String email);
    // Método para verificar se um email já está cadastrado no banco
    // Retorna true se existir algum registro com esse email, false caso contrário

    Boolean existsByTelefone(String telefone);
    // Método opcional para verificar se um telefone já está cadastrado
    // Retorna true se existir algum registro com esse telefone, false caso contrário
}
