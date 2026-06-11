package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Petshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetshopRepository extends JpaRepository<Petshop, Long> {

    Optional<Petshop> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
}
