package com.Mybuddy.Myb.Service;
// Pacote onde a classe PetSpecification está localizada, responsável por criar filtros dinâmicos para consultas de pets

import com.Mybuddy.Myb.Model.Pet;
// Importa a entidade Pet, que representa os dados do pet no banco
import org.springframework.data.jpa.domain.Specification;
// Importa Specification do Spring Data JPA, usada para construir consultas dinâmicas

public final class PetSpecification {
    // Classe final, não pode ser estendida
    private PetSpecification() {}
    // Construtor privado para impedir instâncias, pois a classe só fornece métodos estáticos

    public static Specification<Pet> comFiltros(PetFiltro filtro) {
        // Método principal que retorna uma Specification combinando todos os filtros
        return nomeContem(filtro.nome())            // Filtra por nome (contendo)
                .and(igual("especie", filtro.especie())) // Filtra por espécie exata
                .and(igual("porte", filtro.porte()))     // Filtra por porte exato
                .and(igual("sexo", filtro.sexo()))       // Filtra por sexo exato
                .and(idadeMin(filtro.idadeMin()))        // Filtra por idade mínima
                .and(idadeMax(filtro.idadeMax()));       // Filtra por idade máxima
    }

    private static Specification<Pet> nomeContem(String nome) {
        // Cria uma Specification que busca pets cujo nome contenha o valor passado
        return (root, query, cb) -> (nome == null || nome.isBlank()) ? null
                : cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
        // Se o nome for nulo ou vazio, ignora o filtro (retorna null)
        // cb.like cria uma cláusula LIKE no SQL, e cb.lower transforma em minúsculas para comparação case-insensitive
    }

    private static Specification<Pet> igual(String campo, String valor) {
        // Cria uma Specification que verifica igualdade de um campo com o valor passado
        return (root, query, cb) -> (valor == null || valor.isBlank()) ? null
                : cb.equal(root.get(campo), valor);
        // Se o valor for nulo ou vazio, ignora o filtro (retorna null)
        // cb.equal cria uma cláusula de igualdade no SQL
    }

    private static Specification<Pet> idadeMin(Integer idadeMin) {
        // Cria uma Specification para filtrar pets com idade maior ou igual ao valor passado
        return (root, query, cb) -> idadeMin == null ? null
                : cb.greaterThanOrEqualTo(root.get("idade"), idadeMin);
        // Se idadeMin for nula, ignora o filtro
    }

    private static Specification<Pet> idadeMax(Integer idadeMax) {
        // Cria uma Specification para filtrar pets com idade menor ou igual ao valor passado
        return (root, query, cb) -> idadeMax == null ? null
                : cb.lessThanOrEqualTo(root.get("idade"), idadeMax);
        // Se idadeMax for nula, ignora o filtro
    }
}
