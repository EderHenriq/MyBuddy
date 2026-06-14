package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, Long> {
    Optional<Cupom> findByCodigoAndAtivoTrue(String codigo);
    Optional<Cupom> findByCodigo(String codigo);
    List<Cupom> findByPetshopId(Long petshopId);
    List<Cupom> findByPetshopIdAndAtivoTrue(Long petshopId);
    List<Cupom> findByPetshopIsNullAndAtivoTrue();
}
