package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.Pet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório do MongoDB para a entidade Pet.
 * Todos os logs e comentários seguem os padrões em pt-br.
 */
@Repository
public interface PetRepository extends MongoRepository<Pet, Long> {

    /**
     * Busca um pet pelo seu nome.
     *
     * @param nome O nome do pet.
     * @return Um Optional contendo o Pet caso encontrado.
     */
    Optional<Pet> findByNome(String nome);

    /**
     * Busca todos os pets associados a uma organização específica pelo seu ID.
     * Query derivada resolvida pelo Spring Data MongoDB através da referência.
     *
     * @param organizacaoId O ID da organização.
     * @return Uma lista de Pet pertencentes à organização.
     */
    List<Pet> findByOrganizacaoId(Long organizacaoId);
}
