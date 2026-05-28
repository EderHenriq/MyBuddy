package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.ERole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}
