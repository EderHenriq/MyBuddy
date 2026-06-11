package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.ProdutoRequestDTO;
import com.Mybuddy.Myb.DTO.ProdutoResponseDTO;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Slf4j
public class ProdutoController {

    private final ProdutoService produtoService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> buscarComFiltros(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long subCategoriaId,
            @RequestParam(required = false) Long petshopId,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Buscando produtos com filtros: busca={}, categoriaId={}, subCategoriaId={}, petshopId={}, precoMin={}, precoMax={}",
                busca, categoriaId, subCategoriaId, petshopId, precoMin, precoMax);

        return ResponseEntity.ok(produtoService.buscarComFiltros(busca, categoriaId, subCategoriaId, petshopId, precoMin, precoMax, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("Buscando produto por ID: {}", id);
        return ResponseEntity.ok(produtoService.buscarPorIdDTO(id));
    }

    @GetMapping("/petshop/{petshopId}")
    public ResponseEntity<Page<ProdutoResponseDTO>> buscarPorPetshop(
            @PathVariable Long petshopId,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Buscando produtos para o Petshop ID: {}", petshopId);
        return ResponseEntity.ok(produtoService.buscarPorPetshop(petshopId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('PETSHOP') or hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponseDTO> criar(
            @Valid @RequestBody ProdutoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para cadastrar produto recebida: {}", request.getNome());
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        ProdutoResponseDTO criado = produtoService.criar(request, usuario);
        return ResponseEntity.created(URI.create("/api/produtos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PETSHOP') or hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para atualizar produto ID: {}", id);
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(produtoService.atualizar(id, request, usuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PETSHOP') or hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para deletar produto ID: {}", id);
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        produtoService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
