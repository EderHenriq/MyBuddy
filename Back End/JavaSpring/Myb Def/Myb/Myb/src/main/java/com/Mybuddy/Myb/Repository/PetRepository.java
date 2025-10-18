package com.Mybuddy.Myb.Repository; // Declara o pacote onde esta interface de repositório está localizada.

import com.Mybuddy.Myb.Model.Pet; // Importa a entidade Pet, que é o tipo de objeto que este repositório gerenciará.
import org.springframework.data.jpa.repository.JpaRepository; // Importa a interface JpaRepository do Spring Data JPA.
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Importa a interface JpaSpecificationExecutor do Spring Data JPA.
import org.springframework.stereotype.Repository; // Importa a anotação @Repository do Spring.

// Anotação do Spring que indica que esta interface é um componente de repositório.
// Embora não seja estritamente necessária para interfaces que estendem JpaRepository (pois o Spring já as detecta),
// é uma boa prática para clareza e para permitir a detecção de exceções específicas de persistência.
@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {
    // Declara a interface PetRepository.

    // Ao estender JpaRepository<Pet, Long>, esta interface herda automaticamente uma série de métodos CRUD
    // (Create, Read, Update, Delete) para a entidade Pet, onde Long é o tipo da chave primária (ID) da entidade Pet.
    // O Spring Data JPA implementará esses métodos em tempo de execução.

    // Ao estender JpaSpecificationExecutor<Pet>, esta interface adiciona a capacidade de executar consultas
    // mais complexas usando o padrão Specification do JPA Criteria API.
    // Isso permite construir consultas dinâmicas e reutilizáveis, baseadas em múltiplas condições (filtros).
    // Não há necessidade de adicionar métodos aqui se você for usar apenas as funcionalidades de JpaSpecificationExecutor.
}