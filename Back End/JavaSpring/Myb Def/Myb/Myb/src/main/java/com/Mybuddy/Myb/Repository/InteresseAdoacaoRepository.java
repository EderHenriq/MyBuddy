package com.Mybuddy.Myb.Repository; // Declara o pacote onde esta interface de repositório está localizada.

import com.Mybuddy.Myb.Model.InteresseAdoacao; // Importa a entidade InteresseAdoacao, que é o tipo de objeto que este repositório gerenciará.
import com.Mybuddy.Myb.Model.Pet; // Importa a entidade Pet, usada em métodos de busca específicos.
import com.Mybuddy.Myb.Model.Usuario; // Importa a entidade Usuario, usada em métodos de busca específicos.
import org.springframework.data.jpa.repository.JpaRepository; // Importa a interface JpaRepository do Spring Data JPA.

import java.util.List; // Importa a interface List para lidar com coleções de resultados.

// Declara a interface InteresseAdoacaoRepository.
// Ao estender JpaRepository<InteresseAdoacao, Long>, esta interface automaticamente herda
// uma série de métodos CRUD (Create, Read, Update, Delete) para a entidade InteresseAdoacao,
// onde Long é o tipo da chave primária (ID) da entidade InteresseAdoacao.
// O Spring Data JPA implementará esses métodos em tempo de execução.
public interface InteresseAdoacaoRepository extends JpaRepository<InteresseAdoacao, Long> {

    // Declara um método de consulta personalizado para buscar interesses de um usuário específico.
    // O Spring Data JPA irá automaticamente gerar a implementação deste método
    // para buscar todos os interesses de adoção associados a um determinado objeto Usuario.
    // O nome 'findByUsuario' segue a convenção de nomeação de métodos do Spring Data JPA para criar consultas.
    // Equivalente SQL: SELECT * FROM interesses_adoacao WHERE usuario_id = ?
    List<InteresseAdoacao> findByUsuario(Usuario usuario);

    // Declara um método de consulta personalizado para buscar interesses por ID do usuário.
    // Similar ao anterior, mas aceita diretamente o Long usuarioId em vez do objeto Usuario completo.
    // O Spring Data JPA gerará automaticamente a consulta baseada no relacionamento ManyToOne.
    // Equivalente SQL: SELECT * FROM interesses_adoacao WHERE usuario_id = ?
    List<InteresseAdoacao> findByUsuarioId(Long usuarioId);

    // Declara um método de verificação de existência para prevenir interesses duplicados (BUDDY-77).
    // Este método retorna true se já existir um interesse registrado para a combinação Usuario + Pet fornecida.
    // O Spring Data JPA gerará a implementação que verifica a existência do registro sem retornar o objeto completo.
    // Usado para validação de negócio: um usuário não pode manifestar interesse duplicado no mesmo pet.
    // Equivalente SQL: SELECT COUNT(*) > 0 FROM interesses_adoacao WHERE usuario_id = ? AND pet_id = ?
    boolean existsByUsuarioAndPet(Usuario usuario, Pet pet);
}
