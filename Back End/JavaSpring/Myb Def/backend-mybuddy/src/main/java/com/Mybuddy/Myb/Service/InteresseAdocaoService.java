package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.InteresseAdocaoMapper;
import com.Mybuddy.Myb.DTO.InteresseResponse;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InteresseAdocaoService {

    private final InteresseAdocaoRepository interesseRepo;
    private final UsuarioRepository usuarioRepo;
    private final PetRepository petRepo;

    public InteresseAdocaoService(InteresseAdocaoRepository interesseRepo,
                                  UsuarioRepository usuarioRepo,
                                  PetRepository petRepo) {
        this.interesseRepo = interesseRepo;
        this.usuarioRepo = usuarioRepo;
        this.petRepo = petRepo;
    }

    @Transactional
    public InteresseResponse manifestarInteresse(Long usuarioId, Long petId, String mensagem) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado: " + petId));

        if (!pet.getStatusAdocao().equals(StatusAdocao.DISPONIVEL)) {
            throw new IllegalStateException("Pet não está disponível para adoção no momento");
        }

        if (interesseRepo.existsByUsuarioAndPet(usuario, pet)) {
            throw new IllegalStateException("Você já manifestou interesse neste pet");
        }

        InteresseAdocao interesse = new InteresseAdocao();
        interesse.setUsuario(usuario);
        interesse.setPet(pet);
        interesse.setMensagem(mensagem);
        interesse.setStatus(StatusInteresse.PENDENTE);
        interesse.setCriadoEm(LocalDateTime.now());

        var salvo = interesseRepo.save(interesse);
        return InteresseAdocaoMapper.toResponse(salvo);
    }

    @Transactional
    public InteresseResponse atualizarStatus(Long interesseId, StatusInteresse novoStatus) {
        return atualizarStatus(interesseId, novoStatus, null, true);
    }

    @Transactional
    public InteresseResponse atualizarStatus(Long interesseId, StatusInteresse novoStatus, Long userOrgId, boolean isAdmin) {
        InteresseAdocao interesse = interesseRepo.findById(interesseId)
                .orElseThrow(() -> new IllegalArgumentException("Interesse não encontrado: " + interesseId));

        if (!isAdmin) {
            if (interesse.getPet() == null || interesse.getPet().getOrganizacao() == null ||
                    userOrgId == null || !interesse.getPet().getOrganizacao().getId().equals(userOrgId)) {
                throw new org.springframework.security.authorization.AuthorizationDeniedException(
                        "Você não tem permissão para alterar o status deste interesse de adoção."
                );
            }
        }

        interesse.setStatus(novoStatus);
        interesse.setAtualizadoEm(LocalDateTime.now());

        var salvo = interesseRepo.save(interesse);
        return InteresseAdocaoMapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<InteresseResponse> listarPorUsuario(Long usuarioId) {
        return interesseRepo.findByUsuarioIdWithFetch(usuarioId)
                .stream()
                .map(InteresseAdocaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InteresseResponse> listarTodos() {
        return interesseRepo.findAllWithFetch().stream()
                .map(InteresseAdocaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InteresseResponse> listarInteressesPorOrganizacao(Long organizacaoId) {
        return interesseRepo.findByPetOrganizacaoIdWithFetch(organizacaoId)
                .stream()
                .map(InteresseAdocaoMapper::toResponse)
                .toList();
    }
}
