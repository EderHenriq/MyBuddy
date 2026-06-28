package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.PedidoRequestDTO;
import com.Mybuddy.Myb.DTO.PedidoResponseDTO;
import com.Mybuddy.Myb.Model.StatusPedido;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.PedidoService;
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
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class PedidoController {

    private final PedidoService pedidoService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponseDTO> criar(
            @Valid @RequestBody PedidoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para realizar compra no marketplace recebida.");
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        PedidoResponseDTO criado = pedidoService.criar(request, usuario);
        return ResponseEntity.created(URI.create("/api/pedidos/" + criado.getId())).body(criado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Buscando detalhes do pedido ID: {}", id);
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(pedidoService.buscarPorIdDTO(id, usuario));
    }

    @GetMapping("/meus")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PedidoResponseDTO>> listarMeusPedidos(
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Listando pedidos do cliente logado.");
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(pedidoService.listarPedidosCliente(usuario));
    }

    @GetMapping("/petshop")
    @PreAuthorize("hasRole('PETSHOP') or hasRole('ADMIN')")
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidosPetshop(
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Listando pedidos recebidos pelo petshop logado.");
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(pedidoService.listarPedidosPetshop(usuario));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('PETSHOP') or hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusPedido status,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para alterar status do pedido ID {} para {}", id, status);
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, status, usuario));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponseDTO> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição de cancelamento para o pedido ID: {}", id);
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(pedidoService.cancelar(id, usuario));
    }
}
