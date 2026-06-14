package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.AgendamentoRequestDTO;
import com.Mybuddy.Myb.DTO.AgendamentoResponseDTO;
import com.Mybuddy.Myb.DTO.AtualizarStatusAgendamentoRequest;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.AgendamentoService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
@Slf4j
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AgendamentoResponseDTO> criar(
            @Valid @RequestBody AgendamentoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para criar agendamento recebida.");
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        AgendamentoResponseDTO criado = agendamentoService.criar(request, usuario);
        return ResponseEntity.created(URI.create("/api/agendamentos/" + criado.getId())).body(criado);
    }

    @GetMapping("/cliente")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarPorCliente(@AuthenticationPrincipal Jwt jwt) {
        log.info("Listando agendamentos do cliente logado.");
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(agendamentoService.listarPorCliente(usuario));
    }

    @GetMapping("/petshop")
    @PreAuthorize("hasRole('PETSHOP') or hasRole('ADMIN')")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarPorPetshop(@AuthenticationPrincipal Jwt jwt) {
        log.info("Listando agendamentos do petshop logado.");
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(agendamentoService.listarPorPetshop(usuario));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AgendamentoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusAgendamentoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Atualizando status do agendamento ID: {}", id);
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(agendamentoService.atualizarStatus(id, request.status(), usuario));
    }
}
