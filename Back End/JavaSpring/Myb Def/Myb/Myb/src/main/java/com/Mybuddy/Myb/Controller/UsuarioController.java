package com.Mybuddy.Myb.Controller; // Define o pacote onde esta classe controladora está localizada.

import com.Mybuddy.Myb.Model.Usuario; // Importa a classe de modelo (entidade) Usuario.
import com.Mybuddy.Myb.Service.UsuarioService; // Importa a classe de serviço que contém a lógica de negócio para Usuario.
import org.springframework.beans.factory.annotation.Autowired; // Importa a anotação para injeção de dependência.
import org.springframework.http.HttpStatus; // Importa a enumeração de status HTTP (ex: CREATED, CONFLICT).
import org.springframework.http.ResponseEntity; // Importa a classe para criar respostas HTTP completas (status, cabeçalhos, corpo).
import org.springframework.web.bind.annotation.*; // Importa as anotações do Spring Web para criar controladores REST.

import java.util.List; // Importa a interface List para trabalhar com coleções de objetos.
import java.util.Optional; // Importa a classe Optional, um contêiner que pode ou não conter um valor não nulo.

@RestController // Anotação que marca esta classe como um controlador REST. O Spring sabe que os métodos aqui retornarão dados (como JSON) diretamente no corpo da resposta.
@RequestMapping("/api/usuarios") // Mapeia todas as requisições que começam com "/api/usuarios" para os métodos dentro desta classe.
public class UsuarioController {

    @Autowired // Anotação que realiza a injeção de dependência. O Spring fornecerá automaticamente uma instância de UsuarioService.
    private UsuarioService usuarioService; // Declara uma variável para o serviço de usuário.

    // --- ENDPOINT DE CRIAÇÃO (CREATE) ---
    @PostMapping // Mapeia requisições HTTP do tipo POST para este método. A URL será POST /api/usuarios.
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) { // @RequestBody indica que o corpo da requisição (JSON) será convertido em um objeto Usuario.
        try { // Inicia um bloco try-catch para lidar com possíveis erros vindos da camada de serviço.
            Usuario novoUsuario = usuarioService.criarUsuario(usuario); // Chama o método de serviço para criar o usuário no banco de dados.
            // Se a criação for bem-sucedida, retorna uma resposta HTTP 201 Created com o novo usuário no corpo.
            return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
        } catch (IllegalStateException e) { // Captura a exceção lançada pelo serviço se o e-mail já existir.
            // Se o e-mail já estiver em uso, retorna uma resposta HTTP 409 Conflict sem corpo.
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    // --- ENDPOINTS DE LEITURA (READ) ---
    @GetMapping // Mapeia requisições HTTP do tipo GET para este método. A URL será GET /api/usuarios.
    public List<Usuario> listarUsuarios() {
        // Chama o serviço para buscar todos os usuários e os retorna diretamente. O Spring converte a lista para JSON automaticamente com status 200 OK.
        return usuarioService.buscarTodosUsuarios();
    }

    @GetMapping("/{id}") // Mapeia requisições GET para um usuário específico. Ex: GET /api/usuarios/1.
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) { // @PathVariable extrai o valor do {id} da URL e o passa como parâmetro para o método.
        Optional<Usuario> usuario = usuarioService.buscarUsuarioPorId(id); // Chama o serviço para buscar um usuário por ID. O resultado é um Optional.
        // O método 'map' é executado se o Optional contiver um valor (usuário encontrado).
        // Ele transforma o objeto Usuario em uma ResponseEntity com status 200 OK.
        // O método 'orElseGet' é executado se o Optional estiver vazio (usuário não encontrado),
        // retornando uma ResponseEntity com status 404 Not Found.
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- ENDPOINT DE ATUALIZAÇÃO (UPDATE) ---
    @PutMapping("/{id}") // Mapeia requisições HTTP do tipo PUT para este método. Ex: PUT /api/usuarios/1.
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dadosUsuario) { // Recebe o ID da URL e os novos dados do usuário do corpo da requisição.
        try { // Inicia um bloco try-catch para lidar com o caso de o usuário não ser encontrado.
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, dadosUsuario); // Chama o serviço para atualizar o usuário.
            // Retorna uma resposta HTTP 200 OK com o usuário já atualizado no corpo.
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (IllegalStateException e) { // Captura a exceção lançada pelo serviço se o ID do usuário não for encontrado.
            // Retorna uma resposta HTTP 404 Not Found.
            return ResponseEntity.notFound().build();
        }
    }

    // --- ENDPOINT DE DELEÇÃO (DELETE) ---
    @DeleteMapping("/{id}") // Mapeia requisições HTTP do tipo DELETE para este método. Ex: DELETE /api/usuarios/1.
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) { // Recebe o ID do usuário a ser deletado da URL.
        try { // Inicia um bloco try-catch.
            usuarioService.deletarUsuario(id); // Chama o serviço para deletar o usuário.
            // Retorna uma resposta HTTP 204 No Content, que é o padrão para uma deleção bem-sucedida. Não há corpo na resposta.
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) { // Captura a exceção se o usuário não for encontrado.
            // Retorna uma resposta HTTP 404 Not Found.
            return ResponseEntity.notFound().build();
        }
    }
}