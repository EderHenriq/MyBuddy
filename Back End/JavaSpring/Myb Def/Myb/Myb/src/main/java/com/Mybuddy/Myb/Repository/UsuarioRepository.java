package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByTelefone(String telefone);
}