package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Petshop;
import com.Mybuddy.Myb.Model.StatusAprovacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetshopRepository extends JpaRepository<Petshop, Long> {

    Optional<Petshop> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);

    /** Retorna todos os Petshops com um determinado status de aprovação. */
    List<Petshop> findByStatusAprovacao(StatusAprovacao statusAprovacao);
}

