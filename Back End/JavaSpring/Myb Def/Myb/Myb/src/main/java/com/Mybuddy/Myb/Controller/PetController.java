package com.Mybuddy.Myb.Controller;
// Pacote onde o controlador PetController está localizado, responsável pelos endpoints relacionados a pets

import com.Mybuddy.Myb.DTO.PetRequestDTO; // Importa o DTO para criação/atualização
import com.Mybuddy.Myb.DTO.PetResponse; // Importa o DTO para retorno de informações do Pet
import com.Mybuddy.Myb.Model.Pet; // Importa a entidade Pet (ainda usada para retorno do 'criar', pode ser trocado por PetResponse também)
import com.Mybuddy.Myb.Service.PetFiltro; // Importa a classe PetFiltro, usada para filtros dinâmicos de pesquisa
import com.Mybuddy.Myb.Service.PetService; // Importa o serviço PetService, que contém a lógica de negócio dos pets
import org.springframework.data.domain.Page; // Importa Page, usada para paginação
import org.springframework.data.domain.Pageable; // Importa Pageable, usada para parametrizar paginação e ordenação
import org.springframework.data.web.PageableDefault; // Importa para definir padrão de paginação
import org.springframework.http.HttpStatus; // Importa códigos de status HTTP
import org.springframework.http.ResponseEntity; // Importa ResponseEntity, usada para retornar respostas HTTP
import org.springframework.security.access.prepost.PreAuthorize; // Importa anotação para restrição de acesso baseado em roles
import org.springframework.security.core.Authentication; // Importa Authentication para pegar dados do usuário logado
import org.springframework.security.core.context.SecurityContextHolder; // Importa SecurityContextHolder para acessar o contexto de segurança
import org.springframework.web.bind.annotation.*; // Importa anotações de controlador REST
import org.springframework.web.multipart.MultipartFile; // Importa MultipartFile, usado para upload de arquivos
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl; // Importa UserDetailsImpl para acessar ID do usuário logado

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value; // Importa @Value para injetar propriedades do application.properties


@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<Pet> criar(@RequestBody PetRequestDTO petRequestDTO) {
        Pet criado = petService.criarPet(petRequestDTO);
        return ResponseEntity.created(URI.create("/api/pets/" + criado.getId())).body(criado);
    }

    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Por favor selecione um arquivo para upload.", HttpStatus.BAD_REQUEST);
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "http://localhost:8080/uploads/" + fileName;
            return new ResponseEntity<>(fileUrl, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Falha ao fazer upload da imagem: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PetResponse>> buscarPetsComFiltros(
            PetFiltro filtro,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        // ALTERADO: O service já pode retornar Page<PetResponse> diretamente
        Page<PetResponse> pets = petService.buscarComFiltrosDTO(filtro, pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PetResponse> buscarPorId(@PathVariable Long id) {
        // ALTERADO: O service agora tem um método para buscar por ID e retornar DTO
        return petService.buscarPetPorIdDTO(id) // Você precisará criar este método no PetService
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('ONG') and @petService.isPetOwnedByCurrentUser(#id, authentication.principal.organizacaoId))") // <-- CORRIGIDO AQUI
    public ResponseEntity<Pet> atualizar(@PathVariable Long id, @RequestBody PetRequestDTO petRequestDTO) { // <-- CORRIGIDO AQUI
        Pet atualizado = petService.atualizarPet(id, petRequestDTO); // <-- CORRIGIDO AQUI
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        petService.deletarPet(id);
        return ResponseEntity.noContent().build();
    }
}