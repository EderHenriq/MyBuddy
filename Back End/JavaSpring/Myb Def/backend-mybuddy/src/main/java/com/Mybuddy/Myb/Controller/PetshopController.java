package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Chat;
import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.Produto;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Repository.mongo.ChatRepository;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/petshop")
public class PetshopController {

    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final ChatRepository chatRepository;
    private final KeycloakUserSyncService keycloakUserSyncService;

    public PetshopController(ProdutoRepository produtoRepository, 
                             PedidoRepository pedidoRepository, 
                             ChatRepository chatRepository,
                             KeycloakUserSyncService keycloakUserSyncService) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
        this.chatRepository = chatRepository;
        this.keycloakUserSyncService = keycloakUserSyncService;
    }

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
