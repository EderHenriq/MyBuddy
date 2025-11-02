package com.Mybuddy.Myb.Controller;
// Pacote onde o controlador PetController está localizado, responsável pelos endpoints relacionados a pets

import com.Mybuddy.Myb.DTO.PetResponse;
import com.Mybuddy.Myb.Model.Pet;
// Importa a entidade Pet
import com.Mybuddy.Myb.Service.PetFiltro;
// Importa a classe PetFiltro, usada para filtros dinâmicos de pesquisa
import com.Mybuddy.Myb.Service.PetService;
// Importa o serviço PetService, que contém a lógica de negócio dos pets
import org.springframework.data.domain.Page;
// Importa Page, usada para paginação
import org.springframework.data.domain.Pageable;
// Importa Pageable, usada para parametrizar paginação e ordenação
import org.springframework.http.HttpStatus;
// Importa códigos de status HTTP
import org.springframework.http.ResponseEntity;
// Importa ResponseEntity, usada para retornar respostas HTTP
import org.springframework.security.access.prepost.PreAuthorize;
// Importa anotação para restrição de acesso baseado em roles
import org.springframework.web.bind.annotation.*;
// Importa anotações de controlador REST
import org.springframework.data.web.PageableDefault;
// Importa para definir padrão de paginação

import java.net.URI;
// Importa URI para criar links de recurso criado
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
// Importa MultipartFile, usado para upload de arquivos
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
// Importa @Value para injetar propriedades do application.properties
import org.springframework.security.core.Authentication;
// Importa Authentication para pegar dados do usuário logado
import org.springframework.security.core.context.SecurityContextHolder;
// Importa SecurityContextHolder para acessar o contexto de segurança
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
// Importa UserDetailsImpl para acessar ID do usuário logado

@RestController
// Indica que esta classe é um controlador REST
@RequestMapping("/api/pets")
// Define a URL base para todos os endpoints deste controlador
public class PetController {

    private final PetService petService;
    // Serviço responsável pela lógica de negócios de pets

    @Value("${file.upload-dir}")
    private String uploadDir;
    // Caminho do diretório de upload de arquivos, configurado no application.properties

    public PetController(PetService petService) {
        this.petService = petService;
    }
    // Construtor para injetar o PetService

    @PostMapping
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    // Apenas ONGs ou ADMIN podem criar pets
    public ResponseEntity<Pet> criar(@RequestBody Pet pet) {
        Pet criado = petService.criarPet(pet);
        // Chama o serviço para criar o pet
        return ResponseEntity.created(URI.create("/api/pets/" + criado.getId())).body(criado);
        // Retorna HTTP 201 Created com o pet criado e URI do recurso
    }

    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    // Apenas ONGs ou ADMIN podem fazer upload de imagens
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Por favor selecione um arquivo para upload.", HttpStatus.BAD_REQUEST);
            // Retorna 400 se o arquivo estiver vazio
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            // Cria diretório se não existir

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            // Gera um nome único para o arquivo
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            // Salva o arquivo no diretório de upload

            String fileUrl = "http://localhost:8080/uploads/" + fileName;
            return new ResponseEntity<>(fileUrl, HttpStatus.OK);
            // Retorna URL do arquivo

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Falha ao fazer upload da imagem: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            // Retorna 500 em caso de erro
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PetResponse>> buscarPetsComFiltros(
            PetFiltro filtro,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<PetResponse> pets = petService.buscarComFiltros(filtro, pageable)
                .map(p -> new PetResponse(
                        p.getId(),
                        p.getNome(),
                        p.getEspecie(),
                        p.getRaca(),
                        p.getIdade(),
                        p.getPorte(),
                        p.getCor(),
                        p.getSexo(),
                        p.getImageUrl(),
                        p.getStatusAdocao(),
                        p.getOrganizacao() != null ? p.getOrganizacao().getNomeFantasia() : null
                ));

        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PetResponse> buscarPorId(@PathVariable Long id) {
        return petService.buscarPetPorId(id)
                .map(p -> new PetResponse(
                        p.getId(),
                        p.getNome(),
                        p.getEspecie(),
                        p.getRaca(),
                        p.getIdade(),
                        p.getPorte(),
                        p.getCor(),
                        p.getSexo(),
                        p.getImageUrl(),
                        p.getStatusAdocao(),
                        p.getOrganizacao() != null ? p.getOrganizacao().getNomeFantasia() : null
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    // Apenas ADMIN ou ONG que possui o pet podem atualizar
    @PreAuthorize("hasRole('ADMIN') or (hasRole('ONG') and @petService.isPetOwnedByCurrentUser(#id, authentication.principal.id))")
    public ResponseEntity<Pet> atualizar(@PathVariable Long id, @RequestBody Pet dadosPet) {
        Pet atualizado = petService.atualizarPet(id, dadosPet);
        // Atualiza o pet
        return ResponseEntity.ok(atualizado);
        // Retorna o pet atualizado
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // Apenas ADMIN pode deletar pets
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        petService.deletarPet(id);
        return ResponseEntity.noContent().build();
        // Retorna 204 No Content após exclusão
    }
}
