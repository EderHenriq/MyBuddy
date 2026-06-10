package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.EventoOng;
import com.Mybuddy.Myb.Model.InteresseAdocao;
import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Repository.mongo.EventoOngRepository;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ong")
public class OngController {

    private final InteresseAdocaoRepository interesseAdocaoRepository;
    private final EventoOngRepository eventoOngRepository;
    private final PetRepository petRepository;
    private final KeycloakUserSyncService keycloakUserSyncService;

    public OngController(InteresseAdocaoRepository interesseAdocaoRepository, 
                         EventoOngRepository eventoOngRepository, 
                         PetRepository petRepository,
                         KeycloakUserSyncService keycloakUserSyncService) {
        this.interesseAdocaoRepository = interesseAdocaoRepository;
        this.eventoOngRepository = eventoOngRepository;
        this.petRepository = petRepository;
        this.keycloakUserSyncService = keycloakUserSyncService;
    }

    @GetMapping("/solicitacoes")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<List<InteresseAdocao>> getSolicitacoes(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        
        if (isAdmin) {
            return ResponseEntity.ok(interesseAdocaoRepository.findAll());
        }
        
        if (usuario.getOrganizacao() == null) {
            return ResponseEntity.ok(List.of());
        }
        
        return ResponseEntity.ok(interesseAdocaoRepository.findByPetOrganizacaoId(usuario.getOrganizacao().getId()));
    }

    @GetMapping("/eventos")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<List<EventoOng>> getEventos() {
        return ResponseEntity.ok(eventoOngRepository.findAll());
    }

    @GetMapping("/pets")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<List<Pet>> getPets(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        
        if (isAdmin) {
            return ResponseEntity.ok(petRepository.findAll());
        }
        
        if (usuario.getOrganizacao() == null) {
            return ResponseEntity.ok(List.of());
        }
        
        return ResponseEntity.ok(petRepository.findByOrganizacaoId(usuario.getOrganizacao().getId()));
    }
}
