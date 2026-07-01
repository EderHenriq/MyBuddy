package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.CupomRequestDTO;
import com.Mybuddy.Myb.DTO.CupomResponseDTO;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.CupomService;
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
@RequestMapping("/api/cupons")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class CupomController {

    private final CupomService cupomService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    /**
     * Cria um novo cupom de desconto vinculado a um petshop.
     *
     * @param request dados do cupom
     * @param jwt token do usuário autenticado
     * @return cupom criado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PETSHOP')")
    public ResponseEntity<CupomResponseDTO> criar(
            @Valid @RequestBody CupomRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para criar cupom: {}", request.getCodigo());
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        CupomResponseDTO criado = cupomService.criar(request, usuario);
        return ResponseEntity.created(URI.create("/api/cupons/" + criado.getId())).body(criado);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CupomResponseDTO>> listar(@AuthenticationPrincipal Jwt jwt) {
        log.info("Listando cupons.");
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(cupomService.listar(usuario));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PETSHOP')")
    public ResponseEntity<CupomResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam boolean ativo,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Alterando status do cupom ID {} para {}", id, ativo);
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(cupomService.alterarStatus(id, ativo, usuario));
    }

    @GetMapping("/validar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CupomResponseDTO> validar(
            @RequestParam String codigo,
            @RequestParam Long petshopId) {
        log.info("Validando cupom {} para o petshop ID {}", codigo, petshopId);
        return ResponseEntity.ok(cupomService.buscarPorCodigoValido(codigo, petshopId));
    }
}
