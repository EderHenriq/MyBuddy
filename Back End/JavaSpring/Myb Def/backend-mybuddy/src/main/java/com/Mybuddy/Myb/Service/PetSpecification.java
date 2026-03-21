package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Especie;
import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Model.Porte;
import com.Mybuddy.Myb.Model.StatusAdocao;
import org.springframework.data.jpa.domain.Specification;

public final class PetSpecification {

    private PetSpecification() {}

    public static Specification<Pet> comFiltros(PetFiltro filtro) {
        Specification<Pet> spec = Specification.where(null); // Começa com uma Specification vazia

        // Filtro por nome (contendo, case-insensitive)
        if (filtro.nome() != null && !filtro.nome().isBlank()) {
            spec = spec.and(nomeContem(filtro.nome()));
        }

        // Filtro por espécie (Enum)
        if (filtro.especie() != null) {
            spec = spec.and(igualEspecie(filtro.especie()));
        }

        // Filtro por porte (Enum)
        if (filtro.porte() != null) {
            spec = spec.and(igualPorte(filtro.porte()));
        }

        // Filtro por sexo exato (case-insensitive)
        if (filtro.sexo() != null && !filtro.sexo().isBlank()) {
            spec = spec.and(igualCaseInsensitive("sexo", filtro.sexo()));
        }

        // Filtro por idade mínima
        if (filtro.idadeMin() != null) {
            spec = spec.and(idadeMin(filtro.idadeMin()));
        }

        // Filtro por idade máxima
        if (filtro.idadeMax() != null) {
            spec = spec.and(idadeMax(filtro.idadeMax()));
        }

        // NOVO: Filtro por StatusAdocao
        // O PetFiltro já garante que statusAdocao não será nulo (padrão DISPONIVEL)
        if (filtro.statusAdocao() != null) {
            spec = spec.and(igualStatusAdocao(filtro.statusAdocao()));
        }

        return spec;
    }

    private static Specification<Pet> nomeContem(String nome) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    // NOVO: Método para igualdade case-insensitive em campos String
    private static Specification<Pet> igualCaseInsensitive(String campo, String valor) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get(campo)), valor.toLowerCase());
    }

    // Método para igualdade de Especie (Enum)
    private static Specification<Pet> igualEspecie(Especie especie) {
        return (root, query, cb) -> cb.equal(root.get("especie"), especie);
    }

    // Método para igualdade de Porte (Enum)
    private static Specification<Pet> igualPorte(Porte porte) {
        return (root, query, cb) -> cb.equal(root.get("porte"), porte);
    }

    // Método para igualdade de StatusAdocao (Enum)
    private static Specification<Pet> igualStatusAdocao(StatusAdocao statusAdocao) {
        return (root, query, cb) -> cb.equal(root.get("statusAdocao"), statusAdocao);
    }

    private static Specification<Pet> idadeMin(Integer idadeMin) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("idade"), idadeMin);
    }

    private static Specification<Pet> idadeMax(Integer idadeMax) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("idade"), idadeMax);
    }
}