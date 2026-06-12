package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNome(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}
