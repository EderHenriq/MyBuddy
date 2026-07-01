package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.CupomRequestDTO;
import com.Mybuddy.Myb.DTO.CupomResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Cupom;
import com.Mybuddy.Myb.Model.CupomUsuario;
import com.Mybuddy.Myb.Model.Petshop;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.jpa.CupomRepository;
import com.Mybuddy.Myb.Repository.jpa.CupomUsuarioRepository;
import com.Mybuddy.Myb.Repository.jpa.PetshopRepository;
import com.Mybuddy.Myb.Security.ERole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de gestão de cupons de desconto.
 *
 * Regras de negócio implementadas:
 *  - Cupom deve estar ativo para ser utilizado
 *  - Cupom deve estar dentro do período de validade (dataInicio / dataExpiracao)
 *  - Cupom possui limite de uso geral (limiteUsoGeral); null = ilimitado
 *  - Cada usuário pode usar o mesmo cupom apenas UMA VEZ (uso único por CPF/Usuário)
 *  - Cupom de petshop específico só é válido para compras naquele petshop
 *  - Valor do pedido deve ser >= valorMinimoPedido (quando configurado)
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CupomService {

    private final CupomRepository cupomRepository;
    private final CupomUsuarioRepository cupomUsuarioRepository;
    private final PetshopRepository petshopRepository;

    /**
     * Cria um novo cupom de desconto, restrito a administradores ou petshops donos do cupom.
     *
     * @param request dados do cupom a ser criado
     * @param usuario usuário autenticado que está criando o cupom
     * @return cupom criado
     */
    @Transactional
    public CupomResponseDTO criar(CupomRequestDTO request, Usuario usuario) {
        validarAcessoAdminOuPetshop(usuario);

        String codigoFormatado = request.getCodigo().trim().toUpperCase();

        if (cupomRepository.findByCodigo(codigoFormatado).isPresent()) {
            throw new ConflictException("Já existe um cupom cadastrado com o código: " + codigoFormatado);
        }

        // Valida coerência das datas caso ambas sejam informadas
        if (request.getDataInicio() != null && request.getDataExpiracao() != null
                && request.getDataExpiracao().isBefore(request.getDataInicio())) {
            throw new IllegalArgumentException("A data de expiração não pode ser anterior à data de início do cupom.");
        }

        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        Long petshopId = request.getPetshopId();

        if (!isAdmin) {
            if (usuario.getPetshopId() == null) {
                throw new IllegalArgumentException("O usuário não possui um petshop cadastrado.");
            }
            petshopId = usuario.getPetshopId();
        }

        Petshop petshop = null;
        if (petshopId != null) {
            final Long effectivePetshopId = petshopId;
            petshop = petshopRepository.findById(effectivePetshopId)
                    .orElseThrow(() -> new ResourceNotFoundException("Petshop não encontrado com ID: " + effectivePetshopId));
        }

        Cupom cupom = Cupom.builder()
                .codigo(codigoFormatado)
                .percentualDesconto(request.getPercentualDesconto())
                .petshop(petshop)
                .ativo(request.getAtivo() == null || request.getAtivo())
                .dataInicio(request.getDataInicio())
                .dataExpiracao(request.getDataExpiracao())
                .valorMinimoPedido(request.getValorMinimoPedido())
                .limiteUsoGeral(request.getLimiteUsoGeral())
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
            // Adotantes/outros usuários: lista os globais ativos e no período de validade
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

    /**
     * Valida e retorna um cupom aplicável a um pedido, verificando todas as regras de negócio:
     * ativo, validade temporal, limite geral, uso único por usuário e valor mínimo.
     * NÃO registra o uso — isso deve ser feito via {@link #registrarUso} após confirmação do pedido.
     *
     * @param codigo        código do cupom (case insensitive)
     * @param petshopId     ID do petshop do pedido (para validação de cupons exclusivos)
     * @param usuarioId     ID do usuário que está aplicando o cupom
     * @param valorPedido   valor bruto do pedido (antes do desconto) para validar valor mínimo
     * @return DTO do cupom validado
     */
    @Transactional(readOnly = true)
    public CupomResponseDTO buscarPorCodigoValido(String codigo, Long petshopId, Long usuarioId, BigDecimal valorPedido) {
        String codigoFormatado = codigo.trim().toUpperCase();
        Cupom cupom = cupomRepository.findByCodigoAndAtivoTrue(codigoFormatado)
                .orElseThrow(() -> new IllegalArgumentException("Cupom inválido ou inativo: " + codigoFormatado));

        // ── Regra 1: validade temporal ──────────────────────────────────────────────
        if (!cupom.estaNoPeríodoVálido()) {
            throw new IllegalArgumentException("O cupom '" + codigoFormatado + "' está fora do período de validade.");
        }

        // ── Regra 2: limite de uso geral ────────────────────────────────────────────
        if (!cupom.possuiUsoDisponivel()) {
            throw new IllegalArgumentException("O cupom '" + codigoFormatado + "' atingiu o limite máximo de usos.");
        }

        // ── Regra 3: uso único por usuário ──────────────────────────────────────────
        if (usuarioId != null && cupomUsuarioRepository.existsByCupomIdAndUsuarioId(cupom.getId(), usuarioId)) {
            throw new ConflictException("Você já utilizou o cupom '" + codigoFormatado + "' anteriormente.");
        }

        // ── Regra 4: cupom exclusivo de petshop ─────────────────────────────────────
        if (cupom.getPetshop() != null && !cupom.getPetshop().getId().equals(petshopId)) {
            throw new IllegalArgumentException("Este cupom de desconto não é válido para compras neste Petshop.");
        }

        // ── Regra 5: valor mínimo do pedido ─────────────────────────────────────────
        if (cupom.getValorMinimoPedido() != null && valorPedido != null
                && valorPedido.compareTo(cupom.getValorMinimoPedido()) < 0) {
            throw new IllegalArgumentException(
                    "O cupom '" + codigoFormatado + "' exige um pedido mínimo de R$ "
                            + cupom.getValorMinimoPedido() + ". Valor atual: R$ " + valorPedido + ".");
        }

        return toResponseDTO(cupom);
    }

    /**
     * Sobrecarga retrocompatível sem usuário e valor de pedido — usada em contextos onde
     * apenas a validade básica é verificada (ex: admin consultando cupom).
     */
    @Transactional(readOnly = true)
    public CupomResponseDTO buscarPorCodigoValido(String codigo, Long petshopId) {
        return buscarPorCodigoValido(codigo, petshopId, null, null);
    }

    /**
     * Registra o uso de um cupom por um usuário após a confirmação do pedido.
     * Deve ser chamado DENTRO de uma transação de criação de pedido.
     * Também incrementa o contador global de uso do cupom.
     *
     * @param cupomId   ID do cupom utilizado
     * @param usuarioId ID do usuário que utilizou
     * @param pedidoId  ID do pedido no qual o cupom foi aplicado
     */
    @Transactional
    public void registrarUso(Long cupomId, Long usuarioId, Long pedidoId) {
        Cupom cupom = cupomRepository.findById(cupomId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupom não encontrado com ID: " + cupomId));

        // Dupla verificação dentro da transação para evitar race condition
        if (!cupom.possuiUsoDisponivel()) {
            throw new ConflictException("O cupom já atingiu o limite máximo de usos enquanto o pedido era processado. Tente outro cupom.");
        }

        if (cupomUsuarioRepository.existsByCupomIdAndUsuarioId(cupomId, usuarioId)) {
            throw new ConflictException("Uso duplicado detectado: o cupom já foi registrado para este usuário.");
        }

        cupom.incrementarUso();
        cupomRepository.save(cupom);

        CupomUsuario registro = CupomUsuario.builder()
                .cupom(cupom)
                .usuarioId(usuarioId)
                .pedidoId(pedidoId)
                .build();
        cupomUsuarioRepository.save(registro);
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
                .dataInicio(c.getDataInicio())
                .dataExpiracao(c.getDataExpiracao())
                .valorMinimoPedido(c.getValorMinimoPedido())
                .limiteUsoGeral(c.getLimiteUsoGeral())
                .usoAtual(c.getUsoAtual())
                .build();
    }
}
