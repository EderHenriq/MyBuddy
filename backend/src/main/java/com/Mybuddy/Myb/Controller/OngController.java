package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.InteresseAdocaoMapper;
import com.Mybuddy.Myb.DTO.InteresseResponse;
import com.Mybuddy.Myb.DTO.PetResponse;
import com.Mybuddy.Myb.Model.EventoOng;
import com.Mybuddy.Myb.Model.InteresseAdocao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Repository.mongo.EventoOngRepository;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ong")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class OngController {

    private final InteresseAdocaoRepository interesseAdocaoRepository;
    private final EventoOngRepository eventoOngRepository;
    private final PetService petService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    @GetMapping("/solicitacoes")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<List<InteresseResponse>> getSolicitacoes(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);

        List<InteresseAdocao> entidades = isAdmin
                ? interesseAdocaoRepository.findAll()
                : (usuario.getOrganizacao() == null
                        ? List.of()
                        : interesseAdocaoRepository.findByPetOrganizacaoId(usuario.getOrganizacao().getId()));

        return ResponseEntity.ok(entidades.stream().map(InteresseAdocaoMapper::toResponse).toList());
    }

    @GetMapping("/eventos")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<List<EventoOng>> getEventos(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        
        if (isAdmin) {
            return ResponseEntity.ok(eventoOngRepository.findAll());
        }
        
        if (usuario.getOrganizacao() == null) {
            return ResponseEntity.ok(List.of());
        }
        
        return ResponseEntity.ok(eventoOngRepository.findByOrganizacaoId(usuario.getOrganizacao().getId()));
    }

    @GetMapping("/pets")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<List<PetResponse>> getPets(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);

        if (isAdmin) {
            return ResponseEntity.ok(petService.listarTodosDTO());
        }

        if (usuario.getOrganizacao() == null) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(petService.buscarPetsPorOrganizacaoId(usuario.getOrganizacao().getId()));
    }
}
