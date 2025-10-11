package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Dto.InteresseAdoacaoMapper;
import com.Mybuddy.Myb.Dto.InteresseResponse;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.InteresseAdoacaoRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import com.Mybuddy.Myb.Repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InteresseAdoacaoService {

    private final InteresseAdoacaoRepository interesseRepo;
    private final UsuarioRepository usuarioRepo;
    private final PetRepository petRepo;

    public InteresseAdoacaoService(InteresseAdoacaoRepository interesseRepo,
                                   UsuarioRepository usuarioRepo,
                                   PetRepository petRepo) {
        this.interesseRepo = interesseRepo;
        this.usuarioRepo = usuarioRepo;
        this.petRepo = petRepo;
    }

    @Transactional
    public InteresseResponse registrarInteresse(Long usuarioId, Long petId, String mensagem) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado: " + petId));

        InteresseAdoacao interesse = new InteresseAdoacao();
        interesse.setUsuario(usuario);
        interesse.setPet(pet);
        interesse.setMensagem(mensagem);
        interesse.setStatus(StatusInteresse.PENDENTE);

        var salvo = interesseRepo.save(interesse);
        return InteresseAdoacaoMapper.toResponse(salvo);
    }

    @Transactional
    public InteresseResponse atualizarStatus(Long interesseId, StatusInteresse novoStatus) {
        InteresseAdoacao interesse = interesseRepo.findById(interesseId)
                .orElseThrow(() -> new IllegalArgumentException("Interesse não encontrado: " + interesseId));
        interesse.setStatus(novoStatus);
        var salvo = interesseRepo.save(interesse);
        return InteresseAdoacaoMapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<InteresseResponse> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
        return interesseRepo.findByUsuario(usuario)
                .stream()
                .map(InteresseAdoacaoMapper::toResponse)
                .toList();
    }
}
