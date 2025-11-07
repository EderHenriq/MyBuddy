package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List; // Importa List para o retorno do novo método

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {

    /**
     * Busca todos os pets associados a uma organização específica pelo seu ID.
     * Esta é uma query derivada do Spring Data JPA.
     * Assume que a entidade Pet tem um relacionamento com Organizacao e o campo ID da organizacao
     * pode ser acessado através de 'pet.organizacao.id'.
     *
     * @param organizacaoId O ID da organização.
     * @return Uma lista de Pet pertencentes à organização.
     */
    List<Pet> findByOrganizacaoId(Long organizacaoId);
}