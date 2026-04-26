package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.AtualizarStatusRequest;
import com.Mybuddy.Myb.DTO.InteresseResponse;
import com.Mybuddy.Myb.DTO.RegistrarInteresseRequest;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.InteresseAdocaoService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InteresseAdocaoController {

    private static final Logger logger = LoggerFactory.getLogger(InteresseAdocaoController.class);

    private final InteresseAdocaoService service;
    private final KeycloakUserSyncService keycloakUserSyncService;

    public InteresseAdocaoController(InteresseAdocaoService service, KeycloakUserSyncService keycloakUserSyncService) {
        this.service = service;
        this.keycloakUserSyncService = keycloakUserSyncService;
    }

    @PostMapping("/interesses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InteresseResponse> manifestarInteresse(
            @RequestBody @Valid RegistrarInteresseRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        logger.debug("manifestarInteresse: Usuario ID: {}, Email: {}", usuario.getId(), usuario.getEmail());
        var resp = service.manifestarInteresse(usuario.getId(), req.petId(), req.mensagem());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/interesses/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ONG')")
    public ResponseEntity<InteresseResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarStatusRequest req) {
        logger.debug("atualizarStatus: MÉTODO ACESSADO!");
        var resp = service.atualizarStatus(id, req.status());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/usuarios/me/interesses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InteresseResponse>> listarMeusInteresses(
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        var resp = service.listarPorUsuario(usuario.getId());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/interesses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InteresseResponse>> listarTodos() {
        var resp = service.listarTodos();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/ongs/me/interesses")
    @PreAuthorize("hasRole('ONG')")
    public ResponseEntity<List<InteresseResponse>> listarInteressesDaMinhaOng(
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        if (usuario.getOrganizacao() == null) {
            throw new IllegalStateException("Usuário ONG não possui organização associada.");
        }
        Long organizacaoId = usuario.getOrganizacao().getId();
        logger.debug("listarInteressesDaMinhaOng: ONG Organizacao ID: {}", organizacaoId);
        var resp = service.listarInteressesPorOrganizacao(organizacaoId);
        return ResponseEntity.ok(resp);
    }
}