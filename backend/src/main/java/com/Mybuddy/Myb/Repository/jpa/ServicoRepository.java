package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByPetshopIdAndAtivoTrue(Long petshopId);

    List<Servico> findByPetshopId(Long petshopId);
}
