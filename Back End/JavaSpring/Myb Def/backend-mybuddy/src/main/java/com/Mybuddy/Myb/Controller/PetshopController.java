package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.PetshopRequestDTO;
import com.Mybuddy.Myb.DTO.PetshopResponseDTO;
import com.Mybuddy.Myb.Model.Chat;
import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.Produto;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Repository.mongo.ChatRepository;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.PetshopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/petshop")
@RequiredArgsConstructor
@Slf4j
public class PetshopController {

    private final PetshopService petshopService;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final ChatRepository chatRepository;
    private final KeycloakUserSyncService keycloakUserSyncService;

    @PostMapping
    @PreAuthorize("hasRole('PETSHOP') or hasRole('ADMIN')")
    public ResponseEntity<PetshopResponseDTO> criar(
            @Valid @RequestBody PetshopRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para criar perfil de petshop: {}", request.getNomeFantasia());
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        PetshopResponseDTO criado = petshopService.criar(request, usuario);
        return ResponseEntity.created(URI.create("/api/petshop/" + criado.getId())).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetshopResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("Buscando petshop por ID: {}", id);
        return ResponseEntity.ok(petshopService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<PetshopResponseDTO>> listarTodos() {
        log.info("Buscando lista de todos os petshops.");
        return ResponseEntity.ok(petshopService.listarTodos());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PETSHOP') or hasRole('ADMIN')")
    public ResponseEntity<PetshopResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PetshopRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para atualizar petshop ID: {}", id);
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(petshopService.atualizar(id, request, usuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Requisição para deletar petshop ID: {}", id);
        petshopService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ── LEGACY ENDPOINTS (Compatibilidade com Front) ──────────────────────────

    @GetMapping("/produtos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Produto>> getProdutos(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isPetshop = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_PETSHOP);
        if (isPetshop) {
            if (usuario.getPetshopId() != null) {
                return ResponseEntity.ok(produtoRepository.findByPetshopId(usuario.getPetshopId()));
            }
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(produtoRepository.findAll());
    }

    @GetMapping("/pedidos")
    @PreAuthorize("hasAnyRole('PETSHOP', 'ADMIN')")
    public ResponseEntity<List<Pedido>> getPedidos(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        if (!isAdmin) {
            if (usuario.getPetshopId() != null) {
                return ResponseEntity.ok(pedidoRepository.findByPetshopId(usuario.getPetshopId()));
            }
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(pedidoRepository.findAll());
    }

    @GetMapping("/chats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Chat>> getChats() {
        return ResponseEntity.ok(chatRepository.findAll());
    }
}
