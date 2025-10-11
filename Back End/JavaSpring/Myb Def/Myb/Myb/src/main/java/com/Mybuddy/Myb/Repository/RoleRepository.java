package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}