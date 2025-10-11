package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Model.Usuario; // Certifique-se de que é o nome correto
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email); // Use findByEmail em vez de findByUsername
    Boolean existsByEmail(String email); // Método para verificar se email já existe
    Boolean existsByTelefone(String telefone); // Se quiser verificar se telefone já existe (opcional)
}