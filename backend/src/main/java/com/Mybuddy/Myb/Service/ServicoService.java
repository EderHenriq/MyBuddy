package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.ServicoRequestDTO;
import com.Mybuddy.Myb.DTO.ServicoResponseDTO;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Petshop;
import com.Mybuddy.Myb.Model.Servico;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.jpa.PetshopRepository;
import com.Mybuddy.Myb.Repository.jpa.ServicoRepository;
import com.Mybuddy.Myb.Security.ERole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final PetshopRepository petshopRepository;

    @Transactional
    public ServicoResponseDTO criar(ServicoRequestDTO request, Usuario usuario) {
        if (usuario.getPetshopId() == null) {
            throw new IllegalArgumentException("O usuário logado não possui um petshop cadastrado.");
        }

        Petshop petshop = petshopRepository.findById(usuario.getPetshopId())
                .orElseThrow(() -> new ResourceNotFoundException("Petshop associado ao usuário não encontrado."));

        if (!petshop.isAprovado()) {
            throw new AuthorizationDeniedException("Seu petshop ainda não foi aprovado pela plataforma.");
        }

        Servico servico = new Servico();
        servico.setNome(request.getNome());
        servico.setDescricao(request.getDescricao());
        servico.setPreco(request.getPreco());
        servico.setDuracaoMinutos(request.getDuracaoMinutos());
        servico.setPetshop(petshop);
        servico.setAtivo(request.getAtivo() == null || request.getAtivo());

        return toResponseDTO(servicoRepository.save(servico));
    }

    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> listarPublicosPorPetshop(Long petshopId) {
        return servicoRepository.findByPetshopIdAndAtivoTrue(petshopId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Servico buscarEntidadePorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com ID: " + id));
    }

    boolean podeGerenciarPetshop(Usuario usuario, Long petshopId) {
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        return isAdmin || petshopId.equals(usuario.getPetshopId());
    }

    ServicoResponseDTO toResponseDTO(Servico servico) {
        return ServicoResponseDTO.builder()
                .id(servico.getId())
                .nome(servico.getNome())
                .descricao(servico.getDescricao())
                .preco(servico.getPreco())
                .duracaoMinutos(servico.getDuracaoMinutos())
                .petshopId(servico.getPetshop().getId())
                .petshopNome(servico.getPetshop().getNomeFantasia())
                .ativo(servico.getAtivo())
                .build();
    }
}
