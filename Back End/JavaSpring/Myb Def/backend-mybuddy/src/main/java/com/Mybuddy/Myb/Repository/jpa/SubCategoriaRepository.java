package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.SubCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubCategoriaRepository extends JpaRepository<SubCategoria, Long> {
    Optional<SubCategoria> findByNomeAndCategoriaId(String nome, Long categoriaId);
}
