package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.OrganizacaoRequestDTO;
import com.Mybuddy.Myb.DTO.OrganizacaoResponseDTO;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.OrganizacaoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizacoes")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class OrganizacaoController {

    private static final Logger log = LoggerFactory.getLogger(OrganizacaoController.class);

    private final OrganizacaoService organizacaoService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    /**
     * Cadastra uma nova organização (ONG). Acesso restrito a administradores.
     *
     * @param requestDTO dados da organização a ser criada
     * @return organização criada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizacaoResponseDTO> criarOrganizacao(@Valid @RequestBody OrganizacaoRequestDTO requestDTO) {
        log.info("Criando organização: {}", requestDTO.getNomeFantasia());
        OrganizacaoResponseDTO createdOrganizacao = organizacaoService.criarOrganizacao(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganizacao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizacaoResponseDTO> buscarOrganizacaoPorId(@PathVariable Long id) {
        log.info("Buscando organização ID: {}", id);
        return ResponseEntity.ok(organizacaoService.buscarOrganizacaoPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<OrganizacaoResponseDTO>> listarTodasOrganizacoes() {
        log.info("Listando todas as organizações.");
        return ResponseEntity.ok(organizacaoService.listarTodasOrganizacoes());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ONG')")
    public ResponseEntity<OrganizacaoResponseDTO> atualizarOrganizacao(
            @PathVariable Long id,
            @Valid @RequestBody OrganizacaoRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Atualizando organização ID: {}", id);
        
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == com.Mybuddy.Myb.Security.ERole.ROLE_ADMIN);
        
        if (!isAdmin) {
            if (usuario.getOrganizacao() == null || !usuario.getOrganizacao().getId().equals(id)) {
                throw new AuthorizationDeniedException("Você não tem permissão para atualizar dados desta organização.");
            }
        }
        
        return ResponseEntity.ok(organizacaoService.atualizarOrganizacao(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarOrganizacao(@PathVariable Long id) {
        log.info("Deletando organização ID: {}", id);
        organizacaoService.deletarOrganizacao(id);
        return ResponseEntity.noContent().build();
    }
}