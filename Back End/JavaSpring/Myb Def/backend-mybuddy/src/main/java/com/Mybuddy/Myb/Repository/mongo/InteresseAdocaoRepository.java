package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.InteresseAdocao;
import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Model.StatusInteresse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório do MongoDB para a entidade InteresseAdocao.
 * Todos os logs e comentários seguem os padrões em pt-br.
 */
@Repository
public interface InteresseAdocaoRepository extends MongoRepository<InteresseAdocao, Long> {

    List<InteresseAdocao> findByUsuarioId(Long usuarioId);

    List<InteresseAdocao> findByPetId(Long petId);

    List<InteresseAdocao> findByPetOrganizacaoId(Long organizacaoId);

    List<InteresseAdocao> findByPetOrganizacaoIdAndStatus(Long organizacaoId, StatusInteresse status);

    boolean existsByUsuarioAndPet(Usuario usuario, Pet pet);

    long countByPetId(Long petId);

    default List<InteresseAdocao> findByUsuarioIdWithFetch(Long usuarioId) {
        return findByUsuarioId(usuarioId);
    }

    default List<InteresseAdocao> findAllWithFetch() {
        return findAll();
    }

    default List<InteresseAdocao> findByPetOrganizacaoIdWithFetch(Long organizacaoId) {
        return findByPetOrganizacaoId(organizacaoId);
    }
}
