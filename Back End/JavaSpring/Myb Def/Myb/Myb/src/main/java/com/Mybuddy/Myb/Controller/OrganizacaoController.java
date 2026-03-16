package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.OrganizacaoRequestDTO;
import com.Mybuddy.Myb.DTO.OrganizacaoResponseDTO;
import com.Mybuddy.Myb.Service.OrganizacaoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizacoes")
public class OrganizacaoController {

    private static final Logger logger = LoggerFactory.getLogger(OrganizacaoController.class);

    private final OrganizacaoService organizacaoService;

    public OrganizacaoController(OrganizacaoService organizacaoService) {
        this.organizacaoService = organizacaoService;
    }

    @PostMapping
    public ResponseEntity<OrganizacaoResponseDTO> criarOrganizacao(@Valid @RequestBody OrganizacaoRequestDTO requestDTO) {
        logger.info("Criando organização: {}", requestDTO.getNomeFantasia());
        OrganizacaoResponseDTO createdOrganizacao = organizacaoService.criarOrganizacao(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganizacao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizacaoResponseDTO> buscarOrganizacaoPorId(@PathVariable Long id) {
        logger.info("Buscando organização ID: {}", id);
        return ResponseEntity.ok(organizacaoService.buscarOrganizacaoPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<OrganizacaoResponseDTO>> listarTodasOrganizacoes() {
        logger.info("Listando todas as organizações.");
        return ResponseEntity.ok(organizacaoService.listarTodasOrganizacoes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizacaoResponseDTO> atualizarOrganizacao(@PathVariable Long id,
                                                                       @Valid @RequestBody OrganizacaoRequestDTO requestDTO) {
        logger.info("Atualizando organização ID: {}", id);
        return ResponseEntity.ok(organizacaoService.atualizarOrganizacao(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOrganizacao(@PathVariable Long id) {
        logger.info("Deletando organização ID: {}", id);
        organizacaoService.deletarOrganizacao(id);
        return ResponseEntity.noContent().build();
    }
}