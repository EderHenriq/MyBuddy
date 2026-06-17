package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.ERole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}
