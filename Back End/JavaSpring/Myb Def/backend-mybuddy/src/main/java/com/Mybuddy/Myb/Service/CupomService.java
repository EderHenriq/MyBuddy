package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.CupomRequestDTO;
import com.Mybuddy.Myb.DTO.CupomResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Cupom;
import com.Mybuddy.Myb.Model.Petshop;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.jpa.CupomRepository;
import com.Mybuddy.Myb.Repository.jpa.PetshopRepository;
import com.Mybuddy.Myb.Security.ERole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CupomService {

    private final CupomRepository cupomRepository;
    private final PetshopRepository petshopRepository;

    @Transactional
    public CupomResponseDTO criar(CupomRequestDTO request, Usuario usuario) {
        validarAcessoAdminOuPetshop(usuario);

        String codigoFormatado = request.getCodigo().trim().toUpperCase();

        if (cupomRepository.findByCodigo(codigoFormatado).isPresent()) {
            throw new ConflictException("Já existe um cupom cadastrado com o código: " + codigoFormatado);
        }

        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        Long petshopId = request.getPetshopId();

        if (!isAdmin) {
            // Se for petshop, o cupom deve obrigatoriamente pertencer ao seu petshop
            if (usuario.getPetshopId() == null) {
                throw new IllegalArgumentException("O usuário não possui um petshop cadastrado.");
            }
            petshopId = usuario.getPetshopId();
        }

        Petshop petshop = null;
        if (petshopId != null) {
            petshop = petshopRepository.findById(petshopId)
                    .orElseThrow(() -> new ResourceNotFoundException("Petshop não encontrado com ID: " + request.getPetshopId()));
        }

        Cupom cupom = Cupom.builder()
                .codigo(codigoFormatado)
                .percentualDesconto(request.getPercentualDesconto())
                .petshop(petshop)
                .ativo(request.getAtivo() == null || request.getAtivo())
                .build();

        Cupom salvo = cupomRepository.save(cupom);
        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<CupomResponseDTO> listar(Usuario usuario) {
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        boolean isPetshop = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_PETSHOP);

        List<Cupom> cupons;
        if (isAdmin) {
            cupons = cupomRepository.findAll();
        } else if (isPetshop && usuario.getPetshopId() != null) {
            cupons = cupomRepository.findByPetshopId(usuario.getPetshopId());
        } else {
            // Adotantes/outros usuários: lista os globais ativos
            cupons = cupomRepository.findByPetshopIsNullAndAtivoTrue();
        }

        return cupons.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public CupomResponseDTO alterarStatus(Long id, boolean ativo, Usuario usuario) {
        Cupom cupom = cupomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupom não encontrado com ID: " + id));

        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        boolean isOwner = cupom.getPetshop() != null && cupom.getPetshop().getId().equals(usuario.getPetshopId());

        if (!isAdmin && !isOwner) {
            throw new AuthorizationDeniedException("Você não tem permissão para alterar o status deste cupom.");
        }

        cupom.setAtivo(ativo);
        return toResponseDTO(cupomRepository.save(cupom));
    }

    @Transactional(readOnly = true)
    public CupomResponseDTO buscarPorCodigoValido(String codigo, Long petshopId) {
        String codigoFormatado = codigo.trim().toUpperCase();
        Cupom cupom = cupomRepository.findByCodigoAndAtivoTrue(codigoFormatado)
                .orElseThrow(() -> new IllegalArgumentException("Cupom inválido ou expirado."));

        // Se o cupom for vinculado a um petshop, valida se é o mesmo petshop do pedido
        if (cupom.getPetshop() != null && !cupom.getPetshop().getId().equals(petshopId)) {
            throw new IllegalArgumentException("Este cupom de desconto não é válido para compras neste Petshop.");
        }

        return toResponseDTO(cupom);
    }

    private void validarAcessoAdminOuPetshop(Usuario usuario) {
        boolean authorized = usuario.getRoles().stream()
                .anyMatch(r -> r.getName() == ERole.ROLE_ADMIN || r.getName() == ERole.ROLE_PETSHOP);
        if (!authorized) {
            throw new AuthorizationDeniedException("Acesso negado. Apenas administradores e petshops podem gerenciar cupons.");
        }
    }

    private CupomResponseDTO toResponseDTO(Cupom c) {
        return CupomResponseDTO.builder()
                .id(c.getId())
                .codigo(c.getCodigo())
                .percentualDesconto(c.getPercentualDesconto())
                .petshopId(c.getPetshop() != null ? c.getPetshop().getId() : null)
                .petshopNome(c.getPetshop() != null ? c.getPetshop().getNomeFantasia() : "Global")
                .ativo(c.isAtivo())
                .build();
    }
}
