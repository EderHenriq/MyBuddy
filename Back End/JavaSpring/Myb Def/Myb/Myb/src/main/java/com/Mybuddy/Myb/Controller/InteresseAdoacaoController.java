package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.AtualizarStatusRequest;
import com.Mybuddy.Myb.DTO.InteresseResponse;
import com.Mybuddy.Myb.DTO.RegistrarInteresseRequest;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
import com.Mybuddy.Myb.Service.InteresseAdoacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class InteresseAdoacaoController {

    private final InteresseAdoacaoService service;

    public InteresseAdoacaoController(InteresseAdoacaoService service) {
        this.service = service;
    }

    @PostMapping("/interesses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InteresseResponse> manifestarInteresse(
            @RequestBody @Valid RegistrarInteresseRequest req,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        System.out.println("-----> DEBUG (Controller - manifestarInteresse): MÉTODO ACESSADO!"); // NOVO LOG
        if (userDetails == null) {
            System.err.println("ERRO CRÍTICO (Controller - manifestarInteresse): userDetails é null!");
            throw new IllegalStateException("Detalhes do usuário autenticado não disponíveis.");
        }
        System.out.println("DEBUG (Controller - manifestarInteresse): Usuario ID recuperado: " + userDetails.getId());
        System.out.println("DEBUG (Controller - manifestarInteresse): Usuario Email recuperado: " + userDetails.getEmail());

        Long usuarioId = userDetails.getId();
        var resp = service.manifestarInteresse(usuarioId, req.petId(), req.mensagem());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/interesses/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ONG')")
    public ResponseEntity<InteresseResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarStatusRequest req
    ) {
        System.out.println("-----> DEBUG (Controller - atualizarStatus): MÉTODO ACESSADO!"); // NOVO LOG
        var resp = service.atualizarStatus(id, req.status());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/usuarios/me/interesses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InteresseResponse>> listarMeusInteresses(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new IllegalStateException("Detalhes do usuário autenticado não disponíveis.");
        }

        Long usuarioId = userDetails.getId();
        var resp = service.listarPorUsuario(usuarioId);
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
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails == null) {
            throw new IllegalStateException("Detalhes do usuário autenticado não disponíveis.");
        }
        System.out.println("DEBUG (Controller - listarInteressesDaMinhaOng): ONG Usuario ID recuperado: " + userDetails.getId());
        System.out.println("DEBUG (Controller - listarInteressesDaMinhaOng): ONG Organizacao ID recuperado: " + userDetails.getOrganizacaoId());

        Long organizacaoId = userDetails.getOrganizacaoId();
        var resp = service.listarInteressesPorOrganizacao(organizacaoId);
        return ResponseEntity.ok(resp);
    }
}