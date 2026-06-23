package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.CampanhaDoacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CampanhaDoacaoRepository extends JpaRepository<CampanhaDoacao, Long> {
    List<CampanhaDoacao> findByOrganizacaoId(Long organizacaoId);
    boolean existsByOrganizacaoId(Long organizacaoId);
    List<CampanhaDoacao> findByStatus(String status);
    List<CampanhaDoacao> findByCategoriaAndStatus(String categoria, String status);
    List<CampanhaDoacao> findByStatusAndDataExpiracaoBefore(String status, LocalDateTime limite);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query("UPDATE CampanhaDoacao c SET c.petId = null WHERE c.petId = :petId")
    void nullifyPetId(@org.springframework.data.repository.query.Param("petId") Long petId);
}
