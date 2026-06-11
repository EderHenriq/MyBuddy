package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.ServicoRequestDTO;
import com.Mybuddy.Myb.DTO.ServicoResponseDTO;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.ServicoService;
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
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
@Slf4j
public class ServicoController {

    private final ServicoService servicoService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    @PostMapping
    @PreAuthorize("hasRole('PETSHOP') or hasRole('ADMIN')")
    public ResponseEntity<ServicoResponseDTO> criar(
            @Valid @RequestBody ServicoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para cadastrar serviço recebida: {}", request.getNome());
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        ServicoResponseDTO criado = servicoService.criar(request, usuario);
        return ResponseEntity.created(URI.create("/api/servicos/" + criado.getId())).body(criado);
    }

    @GetMapping("/petshop/{petshopId}")
    public ResponseEntity<List<ServicoResponseDTO>> listarPorPetshop(@PathVariable Long petshopId) {
        log.info("Listando serviços do petshop ID: {}", petshopId);
        return ResponseEntity.ok(servicoService.listarPublicosPorPetshop(petshopId));
    }
}
