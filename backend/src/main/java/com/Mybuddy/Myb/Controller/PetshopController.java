package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.PetshopPublicResponseDTO;
import com.Mybuddy.Myb.DTO.PetshopRequestDTO;
import com.Mybuddy.Myb.DTO.PetshopResponseDTO;
import com.Mybuddy.Myb.Model.Chat;
import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.Produto;
import com.Mybuddy.Myb.Model.StatusAprovacao;
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
@SuppressWarnings("null")
public class PetshopController {

    private final PetshopService petshopService;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final ChatRepository chatRepository;
    private final KeycloakUserSyncService keycloakUserSyncService;

    /**
     * Cria o perfil de petshop do usuário autenticado, ficando pendente de aprovação
     * pelo administrador até que possa operar publicamente na plataforma.
     *
     * @param request dados do petshop a ser criado
     * @param jwt token do usuário autenticado
     * @return petshop criado
     */
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
    public ResponseEntity<PetshopPublicResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("Buscando petshop por ID: {}", id);
        return ResponseEntity.ok(petshopService.buscarPorIdPublico(id));
    }

    /**
     * Listagem pública: retorna apenas Petshops APROVADOS, sem dados sensíveis.
     */
    @GetMapping
    public ResponseEntity<List<PetshopPublicResponseDTO>> listarAprovados() {
        log.info("Listagem pública de petshops aprovados.");
        return ResponseEntity.ok(petshopService.listarAprovados());
    }

    /**
     * Listagem administrativa: retorna TODOS os Petshops (qualquer status).
     */
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PetshopResponseDTO>> listarTodos(@AuthenticationPrincipal Jwt jwt) {
        log.info("Admin: listando todos os petshops.");
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(petshopService.listarTodos(usuario));
    }

    /**
     * Fila de aprovação: retorna Petshops com status PENDENTE_APROVACAO.
     */
    @GetMapping("/admin/pendentes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PetshopResponseDTO>> listarPendentes(@AuthenticationPrincipal Jwt jwt) {
        log.info("Admin: listando petshops pendentes de aprovação.");
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(petshopService.listarPendentes(usuario));
    }

    /**
     * Aprovação/rejeição de Petshop pelo administrador.
     * PATCH /api/petshop/{id}/aprovacao?status=APROVADO
     */
    @PatchMapping("/{id}/aprovacao")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PetshopResponseDTO> alterarStatusAprovacao(
            @PathVariable Long id,
            @RequestParam StatusAprovacao status,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Admin: alterando status de aprovação do petshop ID {} para {}", id, status);
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(petshopService.alterarStatusAprovacao(id, status, usuario));
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
    @PreAuthorize("hasAnyRole('PETSHOP', 'ADMIN')")
    public ResponseEntity<List<Produto>> getProdutos(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        if (isAdmin) {
            return ResponseEntity.ok(produtoRepository.findAll());
        }
        if (usuario.getPetshopId() != null) {
            return ResponseEntity.ok(produtoRepository.findByPetshopId(usuario.getPetshopId()));
        }
        return ResponseEntity.ok(List.of());
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
