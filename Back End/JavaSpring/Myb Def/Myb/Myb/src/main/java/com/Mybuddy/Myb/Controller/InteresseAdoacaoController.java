package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Dto.AtualizarStatusRequest;
import com.Mybuddy.Myb.Dto.InteresseResponse;
import com.Mybuddy.Myb.Dto.RegistrarInteresseRequest;
import com.Mybuddy.Myb.Service.InteresseAdoacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InteresseAdoacaoController {

    private final InteresseAdoacaoService service;

    public InteresseAdoacaoController(InteresseAdoacaoService service) {
        this.service = service;
    }

    // POST /api/interesses (BUDDY-79)
    @PostMapping("/interesses")
    public ResponseEntity<InteresseResponse> registrarInteresse(
            @RequestBody @Valid RegistrarInteresseRequest req) {
        var resp = service.registrarInteresse(req.usuarioId(), req.petId(), req.mensagem());
        return ResponseEntity.ok(resp);
    }

    // PUT /api/interesses/{id}/status (BUDDY-87)
    @PutMapping("/interesses/{id}/status")
    public ResponseEntity<InteresseResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarStatusRequest req) {
        var resp = service.atualizarStatus(id, req.status());
        return ResponseEntity.ok(resp);
    }

    // GET /api/usuarios/me/interesses (BUDDY-91)
    // Trocar a origem do usuarioId pelo SecurityContext quando integrar autenticação
    @GetMapping("/usuarios/me/interesses")
    public ResponseEntity<List<InteresseResponse>> listarMeusInteresses(
            @RequestParam Long usuarioId) {
        var resp = service.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(resp);
    }
}
