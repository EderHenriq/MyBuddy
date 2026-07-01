package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.PetRequestDTO;
import com.Mybuddy.Myb.DTO.PetResponse;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Service.FotoPetService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.PetFiltro;
import com.Mybuddy.Myb.Service.PetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class PetController {

    private static final Logger log = LoggerFactory.getLogger(PetController.class);

    private final PetService petService;
    private final FotoPetService fotoPetService;
    private final KeycloakUserSyncService keycloakUserSyncService;

    /**
     * Cadastra um novo pet disponível para adoção, vinculado à organização informada.
     * Usuários não administradores só podem cadastrar pets para a própria organização.
     *
     * @param petRequestDTO dados do pet a ser criado
     * @param jwt token do usuário autenticado
     * @return pet criado
     */
    @PostMapping
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<PetResponse> criar(
            @Valid @RequestBody PetRequestDTO petRequestDTO,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para criar pet recebida: {}", petRequestDTO.getNome());
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);

        if (!isAdmin) {
            if (usuario.getOrganizacao() == null) {
                throw new IllegalStateException("Usuário do tipo ONG não possui organização associada.");
            }
            if (!usuario.getOrganizacao().getId().equals(petRequestDTO.getOrganizacaoId())) {
                throw new AuthorizationDeniedException("Você não tem permissão para cadastrar pets para outra organização.");
            }
        }

        PetResponse criadoResponse = petService.criarPet(petRequestDTO);
        return ResponseEntity.created(URI.create("/api/pets/" + criadoResponse.id())).body(criadoResponse);
    }

    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('ONG') or hasRole('PETSHOP') or hasRole('ADMIN')")
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

    @PostMapping("/upload-images")
    @PreAuthorize("hasRole('ONG') or hasRole('PETSHOP') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        log.info("Recebida requisição de upload múltiplo: {} arquivos", files.size());
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor selecione pelo menos um arquivo para upload.");
        }
        if (files.size() > 3) {
            return ResponseEntity.badRequest().body("São permitidas no máximo 3 imagens por pet.");
        }
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
        return ResponseEntity.ok(petService.buscarComFiltrosDTO(filtro, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PetResponse> buscarPorId(@PathVariable Long id) {
        log.info("Buscando pet por ID: {}", id);
        return petService.buscarPetPorIdDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ONG')")
    public ResponseEntity<PetResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PetRequestDTO petRequestDTO,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Requisição para atualizar pet ID {}: {}", id, petRequestDTO.getNome());
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);

        if (!isAdmin) {
            if (usuario.getOrganizacao() == null) {
                throw new IllegalStateException("Usuário do tipo ONG não possui organização associada.");
            }
            Long userOrgId = usuario.getOrganizacao().getId();
            if (!petService.isPetOwnedByCurrentUser(id, userOrgId)) {
                throw new AuthorizationDeniedException("Você não tem permissão para atualizar este pet.");
            }
            if (!userOrgId.equals(petRequestDTO.getOrganizacaoId())) {
                throw new AuthorizationDeniedException("Você não tem permissão para transferir este pet para outra organização.");
            }
        }

        return ResponseEntity.ok(petService.atualizarPet(id, petRequestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Requisição para deletar pet ID: {}", id);
        petService.deletarPet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/organizacao/{organizacaoId}")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<List<PetResponse>> getPetsByOrganizacao(
            @PathVariable Long organizacaoId,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);

        if (!isAdmin) {
            if (usuario.getOrganizacao() == null || !usuario.getOrganizacao().getId().equals(organizacaoId)) {
                throw new AuthorizationDeniedException("Você não tem permissão para visualizar pets desta organização.");
            }
        }

        log.info("Buscando pets da organização por ID: {} - solicitado por usuario ID: {}", organizacaoId, usuario.getId());
        return ResponseEntity.ok(petService.buscarPetsPorOrganizacaoId(organizacaoId));
    }
}