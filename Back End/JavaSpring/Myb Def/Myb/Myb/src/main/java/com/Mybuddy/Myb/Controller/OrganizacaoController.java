package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.OrganizacaoRequestDTO;
import com.Mybuddy.Myb.DTO.OrganizacaoResponseDTO;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException; // Precisaremos desta para demonstrar
import com.Mybuddy.Myb.Exception.ConflictException;       // Precisaremos desta para demonstrar
import com.Mybuddy.Myb.Service.OrganizacaoService;
import jakarta.validation.Valid; // Importe para usar a validação de DTOs
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus; // Para códigos HTTP
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping("/api/organizacoes") // Define o caminho base para todos os endpoints neste controller
public class OrganizacaoController {

    private static final Logger logger = LoggerFactory.getLogger(OrganizacaoController.class);

    private final OrganizacaoService organizacaoService; // Renomeado para seguir convenção

    // Injeção de dependência do serviço via construtor
    public OrganizacaoController(OrganizacaoService organizacaoService) {
        this.organizacaoService = organizacaoService;
    }

    /**
     * Endpoint para criar uma nova organização.
     * Recebe um DTO de requisição e retorna um DTO de resposta.
     * @param requestDTO Dados da organização a ser criada, com validação.
     * @return ResponseEntity com o DTO da organização criada e status 201 Created.
     */
    @PostMapping
    public ResponseEntity<OrganizacaoResponseDTO> criarOrganizacao(@Valid @RequestBody OrganizacaoRequestDTO requestDTO) {
        logger.info("Recebida requisição para criar organização: {}", requestDTO.getNomeFantasia());
        // O serviço já trata as exceções de conflito (CNPJ/Email), que serão mapeadas pelo GlobalExceptionHandler
        OrganizacaoResponseDTO createdOrganizacao = organizacaoService.criarOrganizacao(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganizacao); // Retorna 201 Created
    }

    /**
     * Endpoint para buscar uma organização pelo ID.
     * @param id ID da organização.
     * @return ResponseEntity com o DTO da organização encontrada e status 200 OK, ou 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrganizacaoResponseDTO> buscarOrganizacaoPorId(@PathVariable Long id) {
        logger.info("Recebida requisição para buscar organização com ID: {}", id);
        // O serviço lançará ResourceNotFoundException se não encontrar, que será mapeada pelo GlobalExceptionHandler
        OrganizacaoResponseDTO organizacao = organizacaoService.buscarOrganizacaoPorId(id);
        return ResponseEntity.ok(organizacao); // Retorna 200 OK
    }

    /**
     * Endpoint para listar todas as organizações.
     * @return ResponseEntity com uma lista de DTOs de organizações e status 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<OrganizacaoResponseDTO>> listarTodasOrganizacoes() {
        logger.info("Recebida requisição para listar todas as organizações.");
        List<OrganizacaoResponseDTO> organizacoes = organizacaoService.listarTodasOrganizacoes();
        return ResponseEntity.ok(organizacoes); // Retorna 200 OK
    }

    /**
     * Endpoint para atualizar uma organização existente.
     * @param id ID da organização a ser atualizada.
     * @param requestDTO Dados atualizados da organização, com validação.
     * @return ResponseEntity com o DTO da organização atualizada e status 200 OK, ou 404 Not Found, 409 Conflict.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrganizacaoResponseDTO> atualizarOrganizacao(@PathVariable Long id, @Valid @RequestBody OrganizacaoRequestDTO requestDTO) {
        logger.info("Recebida requisição para atualizar organização com ID: {}", id);
        // O serviço já trata as exceções (NotFound, Conflict), que serão mapeadas pelo GlobalExceptionHandler
        OrganizacaoResponseDTO updatedOrganizacao = organizacaoService.atualizarOrganizacao(id, requestDTO);
        return ResponseEntity.ok(updatedOrganizacao); // Retorna 200 OK
    }

    /**
     * Endpoint para deletar uma organização pelo ID.
     * @param id ID da organização a ser deletada.
     * @return ResponseEntity sem conteúdo e status 204 No Content, ou 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOrganizacao(@PathVariable Long id) {
        logger.info("Recebida requisição para deletar organização com ID: {}", id);
        // O serviço lançará ResourceNotFoundException se não encontrar, que será mapeada pelo GlobalExceptionHandler
        organizacaoService.deletarOrganizacao(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}