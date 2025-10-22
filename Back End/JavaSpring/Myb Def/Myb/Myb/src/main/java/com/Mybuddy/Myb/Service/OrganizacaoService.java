package com.Mybuddy.Myb.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Repository.OrganizacaoRepository;
                              
@Service
public class OrganizacaoService {
    private final OrganizacaoRepository repository;

    public OrganizacaoService(OrganizacaoRepository repository) {
        this.repository = repository;
    }

    // Criar ou atualizar
    public Organizacao salvar(Organizacao ong) {
        return repository.save(ong);
    }

    // Buscar todas
    public List<Organizacao> listarTodas() {
        return repository.findAll();
    }

    // Buscar por ID
    public Optional<Organizacao> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Deletar por ID
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
