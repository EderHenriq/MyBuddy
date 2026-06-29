package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.SubCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubCategoriaRepository extends JpaRepository<SubCategoria, Long> {
    Optional<SubCategoria> findByNomeAndCategoriaId(String nome, Long categoriaId);
    boolean existsByNomeAndCategoriaId(String nome, Long categoriaId);
}
