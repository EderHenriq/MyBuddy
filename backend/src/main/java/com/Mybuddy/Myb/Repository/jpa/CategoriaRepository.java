package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNome(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}
