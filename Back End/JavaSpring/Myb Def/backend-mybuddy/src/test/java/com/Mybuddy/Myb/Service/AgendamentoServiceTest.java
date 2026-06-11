package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.AgendamentoRequestDTO;
import com.Mybuddy.Myb.DTO.AgendamentoResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.jpa.AgendamentoRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private ServicoService servicoService;

    private AgendamentoService agendamentoService;
    private Usuario cliente;
    private Usuario petshopUsuario;
    private Servico servico;
    private LocalDateTime inicio;

    @BeforeEach
    void setUp() {
        agendamentoService = new AgendamentoService(agendamentoRepository, servicoService);
        inicio = LocalDateTime.now().plusDays(1);

        cliente = new Usuario();
        cliente.setId(7L);

        petshopUsuario = new Usuario();
        petshopUsuario.setId(9L);
        petshopUsuario.setPetshopId(3L);
        petshopUsuario.setRoles(Set.of(new Role(ERole.ROLE_PETSHOP)));

        Petshop petshop = Petshop.builder()
                .id(3L)
                .nomeFantasia("Petshop Agenda")
                .statusAprovacao(StatusAprovacao.APROVADO)
                .build();

        servico = Servico.builder()
                .id(11L)
                .nome("Banho")
                .preco(new BigDecimal("60.00"))
                .duracaoMinutos(45)
                .petshop(petshop)
                .ativo(true)
                .build();
    }

    @Test
    void criarAgendamento_ComSucesso() {
        AgendamentoRequestDTO request = new AgendamentoRequestDTO(20L, 11L, inicio, "Ana");

        when(servicoService.buscarEntidadePorId(11L)).thenReturn(servico);
        when(agendamentoRepository.existsConflitoPet(20L, inicio, inicio.plusMinutes(45), StatusAgendamento.CANCELADO)).thenReturn(false);
        when(agendamentoRepository.existsConflitoProfissional(3L, "Ana", inicio, inicio.plusMinutes(45), StatusAgendamento.CANCELADO)).thenReturn(false);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> {
            Agendamento agendamento = invocation.getArgument(0);
            agendamento.setId(30L);
            return agendamento;
        });

        AgendamentoResponseDTO response = agendamentoService.criar(request, cliente);

        assertEquals(30L, response.getId());
        assertEquals(inicio.plusMinutes(45), response.getDataHoraFim());
        assertEquals(StatusAgendamento.AGENDADO, response.getStatus());
    }

    @Test
    void criarAgendamento_ComConflitoDoPet_DeveLancarExcecao() {
        AgendamentoRequestDTO request = new AgendamentoRequestDTO(20L, 11L, inicio, "Ana");

        when(servicoService.buscarEntidadePorId(11L)).thenReturn(servico);
        when(agendamentoRepository.existsConflitoPet(20L, inicio, inicio.plusMinutes(45), StatusAgendamento.CANCELADO)).thenReturn(true);

        assertThrows(ConflictException.class, () -> agendamentoService.criar(request, cliente));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void criarAgendamento_ComConflitoDoProfissional_DeveLancarExcecao() {
        AgendamentoRequestDTO request = new AgendamentoRequestDTO(20L, 11L, inicio, "Ana");

        when(servicoService.buscarEntidadePorId(11L)).thenReturn(servico);
        when(agendamentoRepository.existsConflitoPet(20L, inicio, inicio.plusMinutes(45), StatusAgendamento.CANCELADO)).thenReturn(false);
        when(agendamentoRepository.existsConflitoProfissional(3L, "Ana", inicio, inicio.plusMinutes(45), StatusAgendamento.CANCELADO)).thenReturn(true);

        assertThrows(ConflictException.class, () -> agendamentoService.criar(request, cliente));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void atualizarStatus_ClienteSoPodeCancelar() {
        Agendamento agendamento = criarAgendamentoSalvo();

        when(agendamentoRepository.findById(30L)).thenReturn(Optional.of(agendamento));
        when(servicoService.podeGerenciarPetshop(cliente, 3L)).thenReturn(false);

        assertThrows(AuthorizationDeniedException.class,
                () -> agendamentoService.atualizarStatus(30L, StatusAgendamento.CONCLUIDO, cliente));
    }

    @Test
    void atualizarStatus_PetshopPodeConcluir() {
        Agendamento agendamento = criarAgendamentoSalvo();

        when(agendamentoRepository.findById(30L)).thenReturn(Optional.of(agendamento));
        when(servicoService.podeGerenciarPetshop(petshopUsuario, 3L)).thenReturn(true);
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);

        AgendamentoResponseDTO response = agendamentoService.atualizarStatus(30L, StatusAgendamento.CONCLUIDO, petshopUsuario);

        assertEquals(StatusAgendamento.CONCLUIDO, response.getStatus());
    }

    private Agendamento criarAgendamentoSalvo() {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(30L);
        agendamento.setClienteId(7L);
        agendamento.setPetId(20L);
        agendamento.setServico(servico);
        agendamento.setDataHoraInicio(inicio);
        agendamento.setDataHoraFim(inicio.plusMinutes(45));
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setProfissionalNome("Ana");
        return agendamento;
    }
}
