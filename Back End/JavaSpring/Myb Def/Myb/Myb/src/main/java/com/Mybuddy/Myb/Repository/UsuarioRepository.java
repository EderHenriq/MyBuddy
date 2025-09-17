package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {

    // Busca no banco o usuário com este e-mail.
    Optional<Usuario> findByEmail(String email);

}
