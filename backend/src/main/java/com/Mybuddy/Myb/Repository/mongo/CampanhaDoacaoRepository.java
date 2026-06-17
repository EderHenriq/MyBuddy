package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.Model.CampanhaDoacao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CampanhaDoacaoRepository extends MongoRepository<CampanhaDoacao, Long> {
    List<CampanhaDoacao> findByOrganizacaoId(Long organizacaoId);
    List<CampanhaDoacao> findByStatus(String status);
    List<CampanhaDoacao> findByCategoriaAndStatus(String categoria, String status);
    List<CampanhaDoacao> findByStatusAndDataExpiracaoBefore(String status, java.time.LocalDateTime limite);
}
