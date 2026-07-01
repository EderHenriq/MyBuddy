package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.CategoriaRequestDTO;
import com.Mybuddy.Myb.DTO.CategoriaResponseDTO;
import com.Mybuddy.Myb.DTO.SubCategoriaRequestDTO;
import com.Mybuddy.Myb.DTO.SubCategoriaResponseDTO;
import com.Mybuddy.Myb.Service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class CategoriaController {

    private final CategoriaService categoriaService;

    /**
     * Lista todas as categorias cadastradas, incluindo suas subcategorias.
     *
     * @return lista de categorias e subcategorias
     */
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        log.info("Buscando todas as categorias e subcategorias.");
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("Buscando categoria por ID: {}", id);
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> criar(@Valid @RequestBody CategoriaRequestDTO request) {
        log.info("Requisição para criar categoria recebida: {}", request.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criar(request));
    }

    @PostMapping("/subcategorias")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubCategoriaResponseDTO> criarSubcategoria(@Valid @RequestBody SubCategoriaRequestDTO request) {
        log.info("Requisição para criar subcategoria recebida: {}", request.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criarSubcategoria(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Requisição para deletar categoria ID: {}", id);
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/subcategorias/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarSubcategoria(@PathVariable Long id) {
        log.info("Requisição para deletar subcategoria ID: {}", id);
        categoriaService.deletarSubcategoria(id);
        return ResponseEntity.noContent().build();
    }
}
