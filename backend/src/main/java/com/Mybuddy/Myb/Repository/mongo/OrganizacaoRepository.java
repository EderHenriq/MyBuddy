package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.Organizacao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório MongoDB para a entidade Organizacao.
 */
@Repository
public interface OrganizacaoRepository extends MongoRepository<Organizacao, Long> {

    Optional<Organizacao> findByCnpj(String cnpj);

    Optional<Organizacao> findByEmailContato(String emailContato);

    boolean existsByCnpj(String cnpj);

    boolean existsByEmailContato(String emailContato);
}
