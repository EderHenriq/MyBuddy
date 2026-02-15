package com.Mybuddy.Myb.Repository; // Declara o pacote onde esta interface de repositório está localizada.

import com.Mybuddy.Myb.Model.InteresseAdocao; // Importa a entidade InteresseAdocao, que é o tipo de objeto que este repositório irá gerenciar.
import com.Mybuddy.Myb.Model.Pet; // Importa a entidade Pet, usada em métodos de busca personalizados.
import com.Mybuddy.Myb.Model.Usuario; // Importa a entidade Usuario, usada em métodos de busca personalizados.
import org.springframework.data.jpa.repository.JpaRepository; // Importa a interface JpaRepository do Spring Data JPA.

import java.util.List; // Importa a interface List para coleções de resultados.

// Declara a interface do repositório de interesses de adoção.
// Ao estender JpaRepository<InteresseAdocao, Long>, esta interface herda métodos CRUD prontos para a entidade InteresseAdocao.
// O tipo Long representa o tipo do campo ID (chave primária).
public interface InteresseAdocaoRepository extends JpaRepository<InteresseAdocao, Long> {

    // Busca todos os interesses do usuário informado (relacionamento ManyToOne com Usuario).
    // Equivalente SQL: SELECT * FROM interesses_adoacao WHERE usuario_id = ?
    List<InteresseAdocao> findByUsuario(Usuario usuario);

    // Busca todos os interesses de adoção do usuário pelo ID (casos de autenticação JWT).
    // Equivalente SQL: SELECT * FROM interesses_adoacao WHERE usuario_id = ?
    List<InteresseAdocao> findByUsuarioId(Long usuarioId);

    // Verifica se o usuário já manifestou interesse em determinado pet (evita duplicidade).
    // Equivalente SQL: SELECT COUNT(*) > 0 FROM interesses_adoacao WHERE usuario_id = ? AND pet_id = ?
    boolean existsByUsuarioAndPet(Usuario usuario, Pet pet);

    // Busca todos os interesses associados a um pet específico.
    // Equivalente SQL: SELECT * FROM interesses_adoacao WHERE pet_id = ?
    List<InteresseAdocao> findByPet(Pet pet);

    // Conta quantos interesses existem para um determinado pet.
    // Usado para validar exclusão de pet (não pode excluir pet com interesses).
    // Equivalente SQL: SELECT COUNT(*) FROM interesses_adoacao WHERE pet_id = ?
    long countByPetId(Long petId);
}
