package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.CampanhaDoacao;
import com.Mybuddy.Myb.Service.CampanhaDoacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campanhas")
@RequiredArgsConstructor
public class CampanhaDoacaoController {

    private final CampanhaDoacaoService service;

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

    @PostMapping
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<CampanhaDoacao> criar(@RequestBody CampanhaDoacao campanha) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(campanha));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<CampanhaDoacao> atualizar(@PathVariable Long id, @RequestBody CampanhaDoacao campanha) {
        return ResponseEntity.ok(service.atualizar(id, campanha));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
