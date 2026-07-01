package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.InteresseAdocaoMapper;
import com.Mybuddy.Myb.DTO.InteresseResponse;
import com.Mybuddy.Myb.DTO.RegistrarInteresseRequest;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class InteresseAdocaoService {

    private final InteresseAdocaoRepository interesseRepo;
    private final UsuarioRepository usuarioRepo;
    private final PetRepository petRepo;

    /**
     * Registra a manifestação de interesse de um usuário em adotar um pet, validando
     * que o pet esteja disponível e que o usuário ainda não tenha manifestado interesse nele.
     *
     * @param usuarioId identificador do usuário interessado
     * @param req dados do interesse manifestado
     * @return interesse registrado
     */
    @Transactional
    public InteresseResponse manifestarInteresse(Long usuarioId, RegistrarInteresseRequest req) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + usuarioId));

        Pet pet = petRepo.findById(req.petId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + req.petId()));

        if (!pet.getStatusAdocao().equals(StatusAdocao.DISPONIVEL)) {
            throw new IllegalStateException("Pet não está disponível para adoção no momento");
        }

        if (interesseRepo.existsByUsuarioAndPet(usuario, pet)) {
            throw new IllegalStateException("Você já manifestou interesse neste pet");
        }

        InteresseAdocao interesse = new InteresseAdocao();
        interesse.setUsuario(usuario);
        interesse.setPet(pet);
        interesse.setMensagem(req.mensagem());
        interesse.setCpfAdotante(req.cpfAdotante());
        interesse.setIdadeAdotante(req.idadeAdotante());
        interesse.setMotivoAdocao(req.motivoAdocao());
        interesse.setTipoResidencia(req.tipoResidencia());
        interesse.setPossuiTelasProtecao(req.possuiTelasProtecao());
        interesse.setOutrosAnimais(req.outrosAnimais());
        interesse.setTempoSozinhoHoras(req.tempoSozinhoHoras());
        interesse.setTodosCientes(req.todosCientes());
        interesse.setEspacoAdequado(req.espacoAdequado());
        interesse.setConsentimentoLgpd(req.consentimentoLgpd());
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
                .orElseThrow(() -> new ResourceNotFoundException("Interesse não encontrado: " + interesseId));

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

        if (novoStatus == StatusInteresse.APROVADO) {
            Pet pet = interesse.getPet();
            Usuario adotante = interesse.getUsuario();
            if (pet != null) {
                pet.setStatusAdocao(StatusAdocao.ADOTADO);
                pet.setAdotanteId(adotante.getId());
                petRepo.save(pet);

                if (adotante.getPetsAdotadosIds() == null) {
                    adotante.setPetsAdotadosIds(new java.util.HashSet<>());
                }
                adotante.getPetsAdotadosIds().add(pet.getId());
                usuarioRepo.save(adotante);

                List<InteresseAdocao> outros = interesseRepo.findByPetId(pet.getId());
                for (InteresseAdocao outro : outros) {
                    if (!outro.getId().equals(interesseId) && outro.getStatus() == StatusInteresse.PENDENTE) {
                        outro.setStatus(StatusInteresse.REJEITADO);
                        outro.setAtualizadoEm(LocalDateTime.now());
                        interesseRepo.save(outro);
                    }
                }
            }
        }

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
