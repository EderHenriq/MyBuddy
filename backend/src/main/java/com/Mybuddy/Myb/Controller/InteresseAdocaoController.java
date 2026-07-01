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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class InteresseAdocaoController {

    private static final Logger logger = LoggerFactory.getLogger(InteresseAdocaoController.class);

    private final InteresseAdocaoService service;
    private final KeycloakUserSyncService keycloakUserSyncService;

    /**
     * Registra a manifestação de interesse de um usuário autenticado em adotar um pet.
     *
     * @param req dados do interesse manifestado
     * @param jwt token do usuário autenticado
     * @return interesse registrado
     */
    @PostMapping("/interesses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InteresseResponse> manifestarInteresse(
            @RequestBody @Valid RegistrarInteresseRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        logger.debug("manifestarInteresse: Usuario ID: {}", usuario.getId());
        var resp = service.manifestarInteresse(usuario.getId(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/interesses/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ONG')")
    public ResponseEntity<InteresseResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarStatusRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        logger.debug("atualizarStatus: MÉTODO ACESSADO!");
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream()
                .anyMatch(r -> r.getName() == com.Mybuddy.Myb.Security.ERole.ROLE_ADMIN);
        Long userOrgId = (usuario.getOrganizacao() != null) ? usuario.getOrganizacao().getId() : null;

        var resp = service.atualizarStatus(id, req.status(), userOrgId, isAdmin);
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