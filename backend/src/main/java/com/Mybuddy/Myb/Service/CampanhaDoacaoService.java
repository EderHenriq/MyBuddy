package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.CampanhaDoacao;
import com.Mybuddy.Myb.Repository.jpa.CampanhaDoacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CampanhaDoacaoService {

    private final CampanhaDoacaoRepository repository;

    public List<CampanhaDoacao> listarTodas() {
        return repository.findAll();
    }

    public List<CampanhaDoacao> listarAtivas() {
        return repository.findByStatus("ATIVA");
    }

    public List<CampanhaDoacao> listarPorCategoria(String categoria) {
        return repository.findByCategoriaAndStatus(categoria, "ATIVA");
    }

    public List<CampanhaDoacao> listarPorONG(Long organizacaoId) {
        return repository.findByOrganizacaoId(organizacaoId);
    }

    public Optional<CampanhaDoacao> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public CampanhaDoacao criar(CampanhaDoacao campanha) {
        campanha.setArrecadado(BigDecimal.ZERO);
        campanha.setStatus("ATIVA");
        return repository.save(campanha);
    }

    public CampanhaDoacao atualizar(Long id, CampanhaDoacao dadosNovos) {
        return repository.findById(id)
                .map(campanha -> {
                    campanha.setTitulo(dadosNovos.getTitulo());
                    campanha.setDescricao(dadosNovos.getDescricao());
                    campanha.setMeta(dadosNovos.getMeta());
                    campanha.setCategoria(dadosNovos.getCategoria());
                    campanha.setDataExpiracao(dadosNovos.getDataExpiracao());
                    campanha.setStatus(dadosNovos.getStatus());
                    return repository.save(campanha);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Campanha não encontrada: " + id));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public int expirarCampanhasAtivasExpiradas() {
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        List<CampanhaDoacao> expiradas = repository.findByStatusAndDataExpiracaoBefore("ATIVA", agora);
        for (CampanhaDoacao campanha : expiradas) {
            campanha.setStatus("ENCERRADA");
            repository.save(campanha);
        }
        return expiradas.size();
    }
}
