package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.PetshopRequestDTO;
import com.Mybuddy.Myb.DTO.PetshopResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Petshop;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.jpa.PetshopRepository;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetshopService {

    private final PetshopRepository petshopRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PetshopResponseDTO criar(PetshopRequestDTO request, Usuario usuario) {
        if (petshopRepository.existsByCnpj(request.getCnpj())) {
            throw new ConflictException("Já existe um petshop cadastrado com este CNPJ.");
        }

        if (usuario.getPetshopId() != null) {
            throw new IllegalArgumentException("Este usuário já possui um petshop associado.");
        }

        Petshop petshop = Petshop.builder()
                .nomeFantasia(request.getNomeFantasia())
                .emailContato(request.getEmailContato())
                .cnpj(request.getCnpj())
                .telefoneContato(request.getTelefoneContato())
                .endereco(request.getEndereco())
                .descricao(request.getDescricao())
                .website(request.getWebsite())
                .valorMinimoFreteGratis(request.getValorMinimoFreteGratis())
                .build();

        Petshop salvo = petshopRepository.save(petshop);

        // Atualizar o usuário logado com o ID do petshop criado (MongoDB)
        usuario.setPetshopId(salvo.getId());
        usuarioRepository.save(usuario);

        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public PetshopResponseDTO buscarPorId(Long id) {
        Petshop petshop = petshopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Petshop não encontrado com o ID: " + id));
        return toResponseDTO(petshop);
    }

    @Transactional(readOnly = true)
    public List<PetshopResponseDTO> listarTodos() {
        return petshopRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PetshopResponseDTO atualizar(Long id, PetshopRequestDTO request, Usuario usuario) {
        Petshop petshop = petshopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Petshop não encontrado com o ID: " + id));

        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        if (!isAdmin && !id.equals(usuario.getPetshopId())) {
            throw new AuthorizationDeniedException("Você não tem permissão para alterar as informações deste petshop.");
        }

        if (!petshop.getCnpj().equals(request.getCnpj()) && petshopRepository.existsByCnpj(request.getCnpj())) {
            throw new ConflictException("Já existe outro petshop cadastrado com este CNPJ.");
        }

        petshop.setNomeFantasia(request.getNomeFantasia());
        petshop.setEmailContato(request.getEmailContato());
        petshop.setCnpj(request.getCnpj());
        petshop.setTelefoneContato(request.getTelefoneContato());
        petshop.setEndereco(request.getEndereco());
        petshop.setDescricao(request.getDescricao());
        petshop.setWebsite(request.getWebsite());
        petshop.setValorMinimoFreteGratis(request.getValorMinimoFreteGratis());

        return toResponseDTO(petshopRepository.save(petshop));
    }

    @Transactional
    public void deletar(Long id) {
        Petshop petshop = petshopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Petshop não encontrado com o ID: " + id));

        // Desvincular usuários associados a este petshop no MongoDB
        List<Usuario> usuarios = usuarioRepository.findByPetshopId(id);
        for (Usuario u : usuarios) {
            u.setPetshopId(null);
            usuarioRepository.save(u);
        }

        petshopRepository.delete(petshop);
    }

    private PetshopResponseDTO toResponseDTO(Petshop petshop) {
        return PetshopResponseDTO.builder()
                .id(petshop.getId())
                .nomeFantasia(petshop.getNomeFantasia())
                .emailContato(petshop.getEmailContato())
                .cnpj(petshop.getCnpj())
                .telefoneContato(petshop.getTelefoneContato())
                .endereco(petshop.getEndereco())
                .descricao(petshop.getDescricao())
                .website(petshop.getWebsite())
                .valorMinimoFreteGratis(petshop.getValorMinimoFreteGratis())
                .build();
    }
}
