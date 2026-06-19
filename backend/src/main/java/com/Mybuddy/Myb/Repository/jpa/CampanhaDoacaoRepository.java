package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.CampanhaDoacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CampanhaDoacaoRepository extends JpaRepository<CampanhaDoacao, Long> {
    List<CampanhaDoacao> findByOrganizacaoId(Long organizacaoId);
    List<CampanhaDoacao> findByStatus(String status);
    List<CampanhaDoacao> findByCategoriaAndStatus(String categoria, String status);
    List<CampanhaDoacao> findByStatusAndDataExpiracaoBefore(String status, LocalDateTime limite);
}
