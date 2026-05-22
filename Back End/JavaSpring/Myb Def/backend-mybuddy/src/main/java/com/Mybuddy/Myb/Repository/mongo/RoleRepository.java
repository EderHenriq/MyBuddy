package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.ERole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório do MongoDB para a entidade Role.
 * Todos os logs e comentários seguem os padrões em pt-br.
 */
@Repository
public interface RoleRepository extends MongoRepository<Role, Long> {

    /**
     * Busca um perfil/role pelo seu nome do Enum ERole.
     *
     * @param name O nome da role (ERole).
     * @return Um Optional contendo a Role se encontrada.
     */
    Optional<Role> findByName(ERole name);
}
