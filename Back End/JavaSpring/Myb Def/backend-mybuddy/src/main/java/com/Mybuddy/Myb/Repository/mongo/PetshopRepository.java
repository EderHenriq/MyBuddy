package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.Petshop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetshopRepository extends MongoRepository<Petshop, Long> {

    Optional<Petshop> findByCnpj(String cnpj);
}
