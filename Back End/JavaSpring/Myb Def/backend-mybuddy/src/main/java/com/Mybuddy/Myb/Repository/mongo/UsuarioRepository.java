package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByTelefone(String telefone);

    Optional<Usuario> findByKeycloakId(String keycloakId);

    Boolean existsByKeycloakId(String keycloakId);
}
