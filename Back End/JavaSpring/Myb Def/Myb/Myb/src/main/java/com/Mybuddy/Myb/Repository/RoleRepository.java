package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Security.Role; // Ou o pacote onde sua entidade Role está
import com.Mybuddy.Myb.Security.ERole; // <<--- Importe seu ERole
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name); // Busca por ERole
}