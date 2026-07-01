package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.AvaliacaoProdutoRequestDTO;
import com.Mybuddy.Myb.DTO.AvaliacaoProdutoResponseDTO;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.AvaliacaoProdutoService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos/{produtoId}/avaliacoes")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class AvaliacaoProdutoController {

    private final AvaliacaoProdutoService avaliacaoProdutoService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    /**
     * Registra uma nova avaliação para o produto informado.
     *
     * @param produtoId identificador do produto avaliado
     * @param request dados da avaliação (nota e comentário)
     * @param jwt token do usuário autenticado
     * @return avaliação criada
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AvaliacaoProdutoResponseDTO> criar(
            @PathVariable Long produtoId,
            @Valid @RequestBody AvaliacaoProdutoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para avaliar o produto ID: {}", produtoId);
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoProdutoService.criar(produtoId, request, usuario));
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoProdutoResponseDTO>> listarPorProduto(
            @PathVariable Long produtoId) {
        log.info("Buscando avaliações para o produto ID: {}", produtoId);
        return ResponseEntity.ok(avaliacaoProdutoService.listarPorProduto(produtoId));
    }
}
