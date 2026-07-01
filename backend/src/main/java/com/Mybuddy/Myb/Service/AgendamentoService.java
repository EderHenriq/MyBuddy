package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.AgendamentoRequestDTO;
import com.Mybuddy.Myb.DTO.AgendamentoResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Agendamento;
import com.Mybuddy.Myb.Model.Servico;
import com.Mybuddy.Myb.Model.StatusAgendamento;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.jpa.AgendamentoRepository;
import com.Mybuddy.Myb.Security.ERole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ServicoService servicoService;

    /**
     * Cria um novo agendamento para o serviço solicitado, validando disponibilidade
     * e conflitos de horário para o pet e o profissional envolvidos.
     *
     * @param request dados do agendamento
     * @param usuario cliente que está agendando
     * @return agendamento criado
     */
    @Transactional
    public synchronized AgendamentoResponseDTO criar(AgendamentoRequestDTO request, Usuario usuario) {
        if (usuario.getId() == null) {
            throw new IllegalArgumentException("Usuário autenticado inválido para agendamento.");
        }

        Servico servico = servicoService.buscarEntidadePorId(request.getServicoId());

        if (!Boolean.TRUE.equals(servico.getAtivo())) {
            throw new IllegalArgumentException("Serviço indisponível para agendamento.");
        }

        LocalDateTime inicio = request.getDataHoraInicio();
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());

        validarConflitos(request.getPetId(), servico.getPetshop().getId(), request.getProfissionalNome(), inicio, fim);

        Agendamento agendamento = new Agendamento();
        agendamento.setClienteId(usuario.getId());
        agendamento.setPetId(request.getPetId());
        agendamento.setServico(servico);
        agendamento.setDataHoraInicio(inicio);
        agendamento.setDataHoraFim(fim);
        agendamento.setProfissionalNome(normalizarProfissional(request.getProfissionalNome()));
        agendamento.setStatus(StatusAgendamento.AGENDADO);

        return toResponseDTO(agendamentoRepository.save(agendamento));
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponseDTO> listarPorCliente(Usuario usuario) {
        return agendamentoRepository.findByClienteIdOrderByDataHoraInicioDesc(usuario.getId()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponseDTO> listarPorPetshop(Usuario usuario) {
        if (usuario.getPetshopId() == null && !isAdmin(usuario)) {
            throw new IllegalArgumentException("O usuário logado não possui um petshop cadastrado.");
        }

        Long petshopId = usuario.getPetshopId();
        if (petshopId == null) {
            return agendamentoRepository.findAll().stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }

        return agendamentoRepository.findByServicoPetshopIdOrderByDataHoraInicioDesc(petshopId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AgendamentoResponseDTO atualizarStatus(Long id, StatusAgendamento status, Usuario usuario) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado com ID: " + id));

        boolean isCliente = agendamento.getClienteId().equals(usuario.getId());
        boolean podeGerenciarPetshop = servicoService.podeGerenciarPetshop(usuario, agendamento.getServico().getPetshop().getId());

        if (!isCliente && !podeGerenciarPetshop) {
            throw new AuthorizationDeniedException("Você não tem permissão para alterar este agendamento.");
        }

        if (isCliente && !podeGerenciarPetshop && status != StatusAgendamento.CANCELADO) {
            throw new AuthorizationDeniedException("Clientes só podem cancelar agendamentos.");
        }

        agendamento.setStatus(status);
        return toResponseDTO(agendamentoRepository.save(agendamento));
    }

    private void validarConflitos(Long petId, Long petshopId, String profissionalNome, LocalDateTime inicio, LocalDateTime fim) {
        if (agendamentoRepository.existsConflitoPet(petId, inicio, fim, StatusAgendamento.CANCELADO)) {
            throw new ConflictException("O pet selecionado já possui atendimento nesse horário.");
        }

        String profissional = normalizarProfissional(profissionalNome);
        if (profissional != null && agendamentoRepository.existsConflitoProfissional(petshopId, profissional, inicio, fim, StatusAgendamento.CANCELADO)) {
            throw new ConflictException("O profissional selecionado já possui atendimento nesse horário.");
        }
    }

    private String normalizarProfissional(String profissionalNome) {
        if (profissionalNome == null || profissionalNome.isBlank()) {
            return null;
        }
        return profissionalNome.trim();
    }

    private boolean isAdmin(Usuario usuario) {
        return usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
    }

    private AgendamentoResponseDTO toResponseDTO(Agendamento agendamento) {
        Servico servico = agendamento.getServico();
        return AgendamentoResponseDTO.builder()
                .id(agendamento.getId())
                .clienteId(agendamento.getClienteId())
                .petId(agendamento.getPetId())
                .servicoId(servico.getId())
                .servicoNome(servico.getNome())
                .petshopId(servico.getPetshop().getId())
                .petshopNome(servico.getPetshop().getNomeFantasia())
                .preco(servico.getPreco())
                .dataHoraInicio(agendamento.getDataHoraInicio())
                .dataHoraFim(agendamento.getDataHoraFim())
                .status(agendamento.getStatus())
                .profissionalNome(agendamento.getProfissionalNome())
                .build();
    }
}
