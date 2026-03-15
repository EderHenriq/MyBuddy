package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.PetRequestDTO;
import com.Mybuddy.Myb.DTO.PetResponse;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
import com.Mybuddy.Myb.Service.FotoPetService;
import com.Mybuddy.Myb.Service.PetFiltro;
import com.Mybuddy.Myb.Service.PetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private static final Logger log = LoggerFactory.getLogger(PetController.class);

    private final PetService petService;
    private final FotoPetService fotoPetService;

    public PetController(PetService petService, FotoPetService fotoPetService) {
        this.petService = petService;
        this.fotoPetService = fotoPetService;
    }

    /**
     * Cria um novo pet.
     * Requer que o usuário seja 'ONG' ou 'ADMIN'.
     * @param petRequestDTO DTO com os dados do pet, incluindo lista de URLs das fotos (nomes de arquivos).
     * @return ResponseEntity com o PetResponse do pet criado e status 201 Created.
     */
    @PostMapping
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<PetResponse> criar(@Valid @RequestBody PetRequestDTO petRequestDTO) {
        log.info("Requisição para criar pet recebida: {}", petRequestDTO.getNome());
        PetResponse criadoResponse = petService.criarPet(petRequestDTO);
        return ResponseEntity.created(URI.create("/api/pets/" + criadoResponse.id())).body(criadoResponse);
    }

    /**
     * Endpoint para upload de uma única imagem por vez.
     * Retorna o nome do arquivo salvo no servidor.
     * O frontend precisará enviar várias requisições para fazer upload de múltiplas imagens
     * e então enviar os nomes dos arquivos para o PetRequestDTO.
     * @param file O arquivo de imagem.
     * @return O nome do arquivo salvo no servidor ou mensagem de erro.
     */
    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("Recebida requisição de upload de imagem: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            return new ResponseEntity<>("Por favor selecione um arquivo para upload.", HttpStatus.BAD_REQUEST);
        }

        try {
            String fileName = fotoPetService.storeFile(file);
            return new ResponseEntity<>(fileName, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Falha ao fazer upload da imagem: {}", e.getMessage(), e);
            return new ResponseEntity<>("Falha ao fazer upload da imagem: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para upload de múltiplas imagens de uma vez.
     * Retorna a lista de nomes dos arquivos salvos no servidor.
     * @param files Lista de arquivos de imagem (máximo 3).
     * @return Lista com os nomes dos arquivos salvos ou mensagem de erro.
     */
    @PostMapping("/upload-images")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        log.info("Recebida requisição de upload múltiplo: {} arquivos", files.size());

        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor selecione pelo menos um arquivo para upload.");
        }

        if (files.size() > 3) {
            return ResponseEntity.badRequest().body("São permitidas no máximo 3 imagens por pet.");
        }

        // Verifica se algum arquivo está vazio
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Um ou mais arquivos estão vazios.");
            }
        }

        try {
            List<String> fileNames = fotoPetService.storeFiles(files);
            log.info("Upload múltiplo concluído com sucesso: {}", fileNames);
            return ResponseEntity.ok(fileNames);
        } catch (Exception e) {
            log.error("Falha ao fazer upload das imagens: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao fazer upload das imagens: " + e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PetResponse>> buscarPetsComFiltros(
            PetFiltro filtro,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        log.info("Buscando pets com filtros. Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<PetResponse> pets = petService.buscarComFiltrosDTO(filtro, pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PetResponse> buscarPorId(@PathVariable Long id) {
        log.info("Buscando pet por ID: {}", id);
        return petService.buscarPetPorIdDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualiza um pet existente.
     * Requer que o usuário seja 'ADMIN' ou 'ONG' que seja proprietária do pet.
     * @param id ID do pet a ser atualizado.
     * @param petRequestDTO DTO com os dados atualizados do pet, incluindo lista de URLs das fotos (nomes de arquivos).
     * @return ResponseEntity com o PetResponse do pet atualizado e status 200 OK.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('ONG') and @petService.isPetOwnedByCurrentUser(#id, authentication.principal.organizacaoId))")
    public ResponseEntity<PetResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PetRequestDTO petRequestDTO) {
        log.info("Requisição para atualizar pet ID {}: {}", id, petRequestDTO.getNome());
        PetResponse atualizadoResponse = petService.atualizarPet(id, petRequestDTO);
        return ResponseEntity.ok(atualizadoResponse);
    }

    /**
     * Deleta um pet.
     * Requer que o usuário seja 'ADMIN'.
     * @param id ID do pet a ser deletado.
     * @return ResponseEntity com status 204 No Content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Requisição para deletar pet ID: {}", id);
        petService.deletarPet(id);
        return ResponseEntity.noContent().build();
    }

    // --- NOVO ENDPOINT PARA BUSCAR PETS POR ORGANIZAÇÃO ---
    @GetMapping("/organizacao/{organizacaoId}")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<List<PetResponse>> getPetsByOrganizacao(@PathVariable Long organizacaoId,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Para segurança, uma ONG só pode ver os pets da sua própria organização
        // Um ADMIN pode ver os pets de qualquer organização
        // `userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))`
        // verifica se o usuário tem a role ADMIN
        if (userDetails.getOrganizacaoId() != null && !userDetails.getOrganizacaoId().equals(organizacaoId) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("Tentativa de acesso não autorizado aos pets da organização {} pelo usuário {}", organizacaoId, userDetails.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Buscando pets da organização por ID: {}", organizacaoId);
        // AQUI: Chamada ao método que será implementado na sua classe PetService
        List<PetResponse> pets = petService.buscarPetsPorOrganizacaoId(organizacaoId);
        return ResponseEntity.ok(pets);
    }
}