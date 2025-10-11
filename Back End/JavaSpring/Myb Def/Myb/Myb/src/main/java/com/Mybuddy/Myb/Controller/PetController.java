package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Service.PetFiltro;
import com.Mybuddy.Myb.Service.PetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // NOVO IMPORT
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PageableDefault;

import java.net.URI;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;


@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    // POST: Criar um novo pet - APENAS ONG
    @PostMapping
    @PreAuthorize("hasRole('ONG')") // Apenas usuários com a ROLE_ONG podem criar
    public ResponseEntity<Pet> criar(@RequestBody Pet pet) {
        Pet criado = petService.criarPet(pet);
        return ResponseEntity.created(URI.create("/api/pets/" + criado.getId())).body(criado);
    }

    // POST: Upload de imagem - APENAS ONG
    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('ONG')") // Apenas usuários com a ROLE_ONG podem fazer upload de imagem
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

    // GET: Listar Pets com Filtros e Paginação - ACESSÍVEL A TODOS
    @GetMapping
    @PreAuthorize("hasAnyRole('ADOTANTE', 'ONG')") // Ambos podem listar, mas podem ver dados diferentes
    public ResponseEntity<Page<Pet>> buscarPetsComFiltros(
            PetFiltro filtro,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<Pet> pets = petService.buscarComFiltros(filtro, pageable);
        return ResponseEntity.ok(pets);
    }

    // GET: Buscar Pet por ID - ACESSÍVEL A TODOS
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADOTANTE', 'ONG')") // Ambos podem ver detalhes
    public ResponseEntity<Pet> buscarPorId(@PathVariable Long id) {
        return petService.buscarPetPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: Atualizar Pet - APENAS ONG
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ONG')") // Apenas usuários com a ROLE_ONG podem atualizar
    public ResponseEntity<Pet> atualizar(@PathVariable Long id, @RequestBody Pet dadosPet) {
        Pet atualizado = petService.atualizarPet(id, dadosPet);
        return ResponseEntity.ok(atualizado);
    }

    // DELETE: Deletar Pet - APENAS ONG
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ONG')") // Apenas usuários com a ROLE_ONG podem deletar
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        petService.deletarPet(id);
        return ResponseEntity.noContent().build();
    }
}