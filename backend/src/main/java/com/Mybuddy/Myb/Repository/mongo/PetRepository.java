package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.Pet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends MongoRepository<Pet, Long> {

    Optional<Pet> findByNome(String nome);

    List<Pet> findByOrganizacaoId(Long organizacaoId);

    long countByStatusAdocao(com.Mybuddy.Myb.Model.StatusAdocao statusAdocao);
}
