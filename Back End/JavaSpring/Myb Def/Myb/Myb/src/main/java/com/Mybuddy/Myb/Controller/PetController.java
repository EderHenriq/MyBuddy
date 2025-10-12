package com.Mybuddy.Myb.Controller; // Declara o pacote onde este controlador está localizado

import com.Mybuddy.Myb.Model.Pet; // Importa o modelo (entidade) Pet
import com.Mybuddy.Myb.Service.PetFiltro; // Importa a classe DTO (ou objeto de serviço) para filtros de busca de pets
import com.Mybuddy.Myb.Service.PetService; // Importa o serviço que contém a lógica de negócio relacionada aos pets
import org.springframework.data.domain.Page; // Importa a interface Page, usada para paginação de resultados
import org.springframework.data.domain.Pageable; // Importa a interface Pageable, usada para informações de paginação
import org.springframework.http.HttpStatus; // Importa a enumeração HttpStatus para códigos de status HTTP
import org.springframework.http.ResponseEntity; // Importa a classe para construir respostas HTTP
import org.springframework.security.access.prepost.PreAuthorize; // Importa a anotação para controle de acesso baseado em permissões/roles
import org.springframework.web.bind.annotation.*; // Importa todas as anotações do Spring Web para REST controllers
import org.springframework.data.web.PageableDefault; // Importa a anotação para definir valores padrão para a paginação

import java.net.URI; // Importa a classe URI para representar identificadores de recursos
import java.util.List; // Importa a interface List para coleções de objetos

import org.springframework.web.multipart.MultipartFile; // Importa a interface para lidar com upload de arquivos
import java.io.IOException; // Importa a exceção de I/O
import java.nio.file.Files; // Importa a classe utilitária para operações de arquivo
import java.nio.file.Path; // Importa a interface Path para representar caminhos de arquivos
import java.nio.file.Paths; // Importa a classe utilitária para obter caminhos de arquivos
import java.nio.file.StandardCopyOption; // Importa opções padrão para operações de cópia de arquivos
import java.util.UUID; // Importa a classe UUID para gerar identificadores únicos
import org.springframework.beans.factory.annotation.Value; // Importa a anotação para injetar valores de propriedades

// Anotação que indica que esta classe é um controlador REST, combinando @Controller e @ResponseBody
@RestController
// Anotação que mapeia todas as requisições que começam com "/api/pets" para este controlador
@RequestMapping("/api/pets")
public class PetController { // Declara a classe do controlador de Pets

    private final PetService petService; // Declara uma instância do serviço de pets, que é final (não pode ser reatribuída)

    // Injeta o valor da propriedade 'file.upload-dir' definida no arquivo application.properties (ou similar).
    // Esta variável armazenará o caminho do diretório onde os arquivos de upload serão salvos.
    @Value("${file.upload-dir}")
    private String uploadDir; // Variável para armazenar o diretório de upload de arquivos

    // Construtor do controlador. O Spring injeta automaticamente uma instância de PetService aqui.
    public PetController(PetService petService) {
        this.petService = petService; // Atribui a instância do serviço injetada à variável 'petService'
    }

    // Método para criar um novo pet
    // Mapeia requisições HTTP POST para o caminho "/api/pets"
    @PostMapping
    // Anotação de segurança que permite acesso apenas a usuários que possuem a role 'ONG'.
    @PreAuthorize("hasRole('ONG')")
    public ResponseEntity<Pet> criar(@RequestBody Pet pet) { // O corpo da requisição é mapeado para um objeto Pet
        Pet criado = petService.criarPet(pet); // Chama o método 'criarPet' no serviço para salvar o pet
        // Retorna uma resposta HTTP 201 Created, com o URI do novo recurso e o objeto Pet criado no corpo
        return ResponseEntity.created(URI.create("/api/pets/" + criado.getId())).body(criado);
    }

    // Método para fazer upload de imagem para um pet
    // Mapeia requisições HTTP POST para o caminho "/api/pets/upload-image"
    @PostMapping("/upload-image")
    // Anotação de segurança que permite acesso apenas a usuários que possuem a role 'ONG'.
    @PreAuthorize("hasRole('ONG')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) { // Espera um arquivo multipart na requisição
        // Verifica se o arquivo enviado está vazio
        if (file.isEmpty()) {
            // Se estiver vazio, retorna uma resposta HTTP 400 Bad Request
            return new ResponseEntity<>("Por favor selecione um arquivo para upload.", HttpStatus.BAD_REQUEST);
        }

        try {
            // Converte o diretório de upload (configurado em 'uploadDir') para um objeto Path absoluto e normalizado
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            // Cria os diretórios necessários se eles não existirem
            Files.createDirectories(uploadPath);

            // Gera um nome de arquivo único usando UUID e concatena com o nome original do arquivo
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            // Resolve o caminho completo para o novo arquivo no diretório de upload
            Path filePath = uploadPath.resolve(fileName);
            // Copia o conteúdo do arquivo enviado para o caminho destino, substituindo se já existir
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Constrói a URL de acesso público para o arquivo salvo
            String fileUrl = "http://localhost:8080/uploads/" + fileName;
            // Retorna uma resposta HTTP 200 OK com a URL do arquivo no corpo
            return new ResponseEntity<>(fileUrl, HttpStatus.OK);

        } catch (IOException e) { // Captura exceções de I/O que podem ocorrer durante o upload
            e.printStackTrace(); // Imprime o stack trace da exceção para depuração
            // Retorna uma resposta HTTP 500 Internal Server Error com uma mensagem de erro
            return new ResponseEntity<>("Falha ao fazer upload da imagem: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método para listar pets com opções de filtro e paginação
    // Mapeia requisições HTTP GET para o caminho "/api/pets"
    @GetMapping
    // Anotação de segurança que permite acesso a usuários com as roles 'ADOTANTE' ou 'ONG'.
    @PreAuthorize("hasAnyRole('ADOTANTE', 'ONG')")
    public ResponseEntity<Page<Pet>> buscarPetsComFiltros( // Retorna uma página de objetos Pet
                                                           PetFiltro filtro, // Parâmetros de filtro são mapeados para o objeto PetFiltro
                                                           @PageableDefault(size = 10, sort = "id") Pageable pageable) { // Define valores padrão para paginação (10 itens por página, ordenado por ID)

        Page<Pet> pets = petService.buscarComFiltros(filtro, pageable); // Chama o método 'buscarComFiltros' no serviço
        return ResponseEntity.ok(pets); // Retorna uma resposta HTTP 200 OK com a página de pets no corpo
    }

    // Método para buscar um pet por seu ID
    // Mapeia requisições HTTP GET para o caminho "/api/pets/{id}", onde {id} é uma variável de caminho
    @GetMapping("/{id}")
    // Anotação de segurança que permite acesso a usuários com as roles 'ADOTANTE' ou 'ONG'.
    @PreAuthorize("hasAnyRole('ADOTANTE', 'ONG')")
    public ResponseEntity<Pet> buscarPorId(@PathVariable Long id) { // Extrai o ID da URL para o parâmetro 'id'
        // Chama o método 'buscarPetPorId' no serviço, que retorna um Optional<Pet>
        return petService.buscarPetPorId(id)
                .map(ResponseEntity::ok) // Se o pet for encontrado, retorna HTTP 200 OK com o pet no corpo
                .orElse(ResponseEntity.notFound().build()); // Se não encontrado, retorna HTTP 404 Not Found
    }

    // Método para atualizar um pet existente
    // Mapeia requisições HTTP PUT para o caminho "/api/pets/{id}"
    @PutMapping("/{id}")
    // Anotação de segurança que permite acesso apenas a usuários que possuem a role 'ONG'.
    @PreAuthorize("hasRole('ONG')")
    public ResponseEntity<Pet> atualizar(@PathVariable Long id, @RequestBody Pet dadosPet) { // Extrai o ID da URL e o corpo da requisição para um objeto Pet
        Pet atualizado = petService.atualizarPet(id, dadosPet); // Chama o método 'atualizarPet' no serviço
        return ResponseEntity.ok(atualizado); // Retorna uma resposta HTTP 200 OK com o objeto Pet atualizado no corpo
    }

    // Método para deletar um pet
    // Mapeia requisições HTTP DELETE para o caminho "/api/pets/{id}"
    @DeleteMapping("/{id}")
    // Anotação de segurança que permite acesso apenas a usuários que possuem a role 'ONG'.
    @PreAuthorize("hasRole('ONG')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) { // Extrai o ID da URL para o parâmetro 'id'
        petService.deletarPet(id); // Chama o método 'deletarPet' no serviço
        // Retorna uma resposta HTTP 204 No Content, indicando que a operação foi bem-sucedida e não há conteúdo para retornar
        return ResponseEntity.noContent().build();
    }
}