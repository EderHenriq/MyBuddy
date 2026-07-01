package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.CampanhaDoacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Service.CampanhaDoacaoService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campanhas")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CampanhaDoacaoController {

    private final CampanhaDoacaoService service;
    private final KeycloakUserSyncService keycloakUserSyncService;

    @GetMapping
    public ResponseEntity<List<CampanhaDoacao>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Long ongId) {
        
        if (ongId != null) {
            return ResponseEntity.ok(service.listarPorONG(ongId));
        }
        if (categoria != null && !categoria.isEmpty() && !categoria.equalsIgnoreCase("Todos")) {
            return ResponseEntity.ok(service.listarPorCategoria(categoria.toUpperCase()));
        }
        return ResponseEntity.ok(service.listarAtivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampanhaDoacao> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria uma nova campanha de doação vinculada à organização do usuário autenticado.
     *
     * @param campanha dados da campanha a ser criada
     * @param jwt token do usuário autenticado
     * @return campanha criada
     */
    @PostMapping
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<CampanhaDoacao> criar(
            @RequestBody CampanhaDoacao campanha,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);

        if (!isAdmin) {
            if (usuario.getOrganizacao() == null) {
                throw new IllegalStateException("Usuário do tipo ONG não possui organização associada.");
            }
            if (campanha.getOrganizacaoId() == null) {
                campanha.setOrganizacaoId(usuario.getOrganizacao().getId());
            } else if (!usuario.getOrganizacao().getId().equals(campanha.getOrganizacaoId())) {
                throw new org.springframework.security.authorization.AuthorizationDeniedException(
                        "Você não tem permissão para criar campanhas para outra organização."
                );
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(campanha));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<CampanhaDoacao> atualizar(
            @PathVariable Long id,
            @RequestBody CampanhaDoacao campanha,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);

        if (!isAdmin) {
            if (usuario.getOrganizacao() == null) {
                throw new IllegalStateException("Usuário do tipo ONG não possui organização associada.");
            }
            CampanhaDoacao existente = service.buscarPorId(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Campanha não encontrada: " + id));
            
            if (!usuario.getOrganizacao().getId().equals(existente.getOrganizacaoId())) {
                throw new org.springframework.security.authorization.AuthorizationDeniedException(
                        "Você não tem permissão para atualizar campanhas de outra organização."
                );
            }
            campanha.setOrganizacaoId(existente.getOrganizacaoId());
        }
        return ResponseEntity.ok(service.atualizar(id, campanha));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
