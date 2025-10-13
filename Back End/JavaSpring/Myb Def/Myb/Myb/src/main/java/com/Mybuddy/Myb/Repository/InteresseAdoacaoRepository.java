package com.Mybuddy.Myb.Repository; // Declara o pacote onde esta interface de repositório está localizada.

import com.Mybuddy.Myb.Model.InteresseAdoacao; // Importa a entidade InteresseAdoacao, que é o tipo de objeto que este repositório gerenciará.
import com.Mybuddy.Myb.Model.Usuario; // Importa a entidade Usuario, usada em métodos de busca específicos.
import com.Mybuddy.Myb.Model.Pet; // Importa a entidade Pet, usada em métodos de busca específicos.
import org.springframework.data.jpa.repository.JpaRepository; // Importa a interface JpaRepository do Spring Data JPA.

import java.util.List; // Importa a interface List para lidar com coleções de resultados.

// Declara a interface InteresseAdoacaoRepository.
// Ao estender JpaRepository<InteresseAdoacao, Long>, esta interface automaticamente herda
// uma série de métodos CRUD (Create, Read, Update, Delete) para a entidade InteresseAdoacao,
// onde Long é o tipo da chave primária (ID) da entidade InteresseAdoacao.
// O Spring Data JPA implementará esses métodos em tempo de execução.
public interface InteresseAdoacaoRepository extends JpaRepository<InteresseAdoacao, Long> {

    // Declara um método de consulta personalizado.
    // O Spring Data JPA irá automaticamente gerar a implementação deste método
    // para buscar todos os interesses de adoção associados a um determinado objeto Usuario.
    // O nome 'findByUsuario' segue a convenção de nomeação de métodos do Spring Data JPA para criar consultas.
    List<InteresseAdoacao> findByUsuario(Usuario usuario);

    // Declara outro método de consulta personalizado.
    // Similar ao anterior, o Spring Data JPA gerará a implementação para buscar
    // todos os interesses de adoção associados a um determinado objeto Pet.
    // O nome 'findByPet' segue a mesma convenção.
    List<InteresseAdoacao> findByPet(Pet pet);

    // Declara um método de contagem para verificar se existem interesses associados a um pet específico.
// O Spring Data JPA gerará automaticamente a implementação para contar registros.
// Usado na BUDDY-98 para validação antes de excluir um pet: se houver interesses, não pode excluir.
// Equivalente SQL: SELECT COUNT(*) FROM interesses_adoacao WHERE pet_id = ?
    default long countByPetId(Long petId) {
        return 0;
    }
}