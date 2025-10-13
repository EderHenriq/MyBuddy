package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Pet;
import org.springframework.data.jpa.domain.Specification;

public final class PetSpecification {
    private PetSpecification() {}

    public static Specification<Pet> comFiltros(PetFiltro filtro) {
        return nomeContem(filtro.nome())
                .and(igual("especie", filtro.especie()))
                .and(igual("porte", filtro.porte()))
                .and(igual("sexo", filtro.sexo()))
                .and(idadeMin(filtro.idadeMin()))
                .and(idadeMax(filtro.idadeMax()));
    }

    private static Specification<Pet> nomeContem(String nome) {
        return (root, query, cb) -> (nome == null || nome.isBlank()) ? null
                : cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    private static Specification<Pet> igual(String campo, String valor) {
        return (root, query, cb) -> (valor == null || valor.isBlank()) ? null
                : cb.equal(root.get(campo), valor);
    }

    private static Specification<Pet> idadeMin(Integer idadeMin) {
        return (root, query, cb) -> idadeMin == null ? null
                : cb.greaterThanOrEqualTo(root.get("idade"), idadeMin);
    }

    private static Specification<Pet> idadeMax(Integer idadeMax) {
        return (root, query, cb) -> idadeMax == null ? null
                : cb.lessThanOrEqualTo(root.get("idade"), idadeMax);
    }
}
