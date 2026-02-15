package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Model.InteresseAdoacao;
import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Declara a interface do repositório de interesses de adoção.
// Ao estender JpaRepository<InteresseAdoacao, Long>, esta interface herda métodos CRUD prontos para a entidade InteresseAdoacao.
// O tipo Long representa o tipo do campo ID (chave primária).
public interface InteresseAdoacaoRepository extends JpaRepository<InteresseAdoacao, Long> {

    // Busca todos os interesses do usuário informado (relacionamento ManyToOne com Usuario).
    // Equivalente SQL: SELECT * FROM interesses_adoacao WHERE usuario_id = ?
    List<InteresseAdoacao> findByUsuario(Usuario usuario);

    // Busca todos os interesses de adoção do usuário pelo ID (casos de autenticação JWT).
    // Equivalente SQL: SELECT * FROM interesses_adoacao WHERE usuario_id = ?
    List<InteresseAdoacao> findByUsuarioId(Long usuarioId);

    // Busca interesses do usuário com JOIN FETCH para evitar N+1
    @Query("SELECT i FROM InteresseAdoacao i " +
           "JOIN FETCH i.usuario " +
           "JOIN FETCH i.pet " +
           "WHERE i.usuario.id = :usuarioId")
    List<InteresseAdoacao> findByUsuarioIdWithFetch(@Param("usuarioId") Long usuarioId);

    // Busca todos os interesses com JOIN FETCH para evitar N+1
    @Query("SELECT i FROM InteresseAdoacao i " +
           "JOIN FETCH i.usuario " +
           "JOIN FETCH i.pet")
    List<InteresseAdoacao> findAllWithFetch();

    // Verifica se o usuário já manifestou interesse em determinado pet (evita duplicidade).
    // Equivalente SQL: SELECT COUNT(*) > 0 FROM interesses_adoacao WHERE usuario_id = ? AND pet_id = ?
    boolean existsByUsuarioAndPet(Usuario usuario, Pet pet);

    // Busca todos os interesses associados a um pet específico.
    // Equivalente SQL: SELECT * FROM interesses_adoacao WHERE pet_id = ?
    List<InteresseAdoacao> findByPet(Pet pet);

    // Conta quantos interesses existem para um determinado pet.
    // Usado para validar exclusão de pet (não pode excluir pet com interesses).
    // Equivalente SQL: SELECT COUNT(*) FROM interesses_adoacao WHERE pet_id = ?
    long countByPetId(Long petId);

    // Busca todos os interesses de pets de uma organização específica.
    // Usa JOIN FETCH para carregar Usuario e Pet em uma única query, evitando N+1.
    @Query("SELECT i FROM InteresseAdoacao i " +
           "JOIN FETCH i.usuario " +
           "JOIN FETCH i.pet p " +
           "WHERE p.organizacao.id = :organizacaoId")
    List<InteresseAdoacao> findByPetOrganizacaoIdWithFetch(@Param("organizacaoId") Long organizacaoId);
}
