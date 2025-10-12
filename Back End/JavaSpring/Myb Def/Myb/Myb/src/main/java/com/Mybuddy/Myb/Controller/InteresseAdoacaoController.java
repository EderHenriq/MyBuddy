package com.Mybuddy.Myb.Controller; // Declara o pacote onde esta classe está localizada

import com.Mybuddy.Myb.Dto.AtualizarStatusRequest; // Importa o DTO (Data Transfer Object) para requisições de atualização de status
import com.Mybuddy.Myb.Dto.InteresseResponse; // Importa o DTO para respostas de interesse de adoção
import com.Mybuddy.Myb.Dto.RegistrarInteresseRequest; // Importa o DTO para requisições de registro de interesse
import com.Mybuddy.Myb.Service.InteresseAdoacaoService; // Importa a classe de serviço que contém a lógica de negócio
import jakarta.validation.Valid; // Importa a anotação para validação de objetos DTO
import org.springframework.http.ResponseEntity; // Importa a classe para construir respostas HTTP
import org.springframework.web.bind.annotation.*; // Importa todas as anotações do Spring Web para REST controllers

import java.util.List; // Importa a interface List para coleções de objetos

@RestController // Anotação que indica que esta classe é um controlador REST, combinando @Controller e @ResponseBody
@RequestMapping("/api") // Anotação que mapeia todas as requisições que começam com "/api" para este controlador
public class InteresseAdoacaoController { // Declara a classe do controlador

    private final InteresseAdoacaoService service; // Declara uma instância do serviço de interesse de adoção, que é final (não pode ser reatribuída)

    // Construtor do controlador. O Spring injeta automaticamente uma instância de InteresseAdoacaoService aqui.
    public InteresseAdoacaoController(InteresseAdoacaoService service) {
        this.service = service; // Atribui a instância do serviço injetada à variável 'service'
    }

    // POST /api/interesses (BUDDY-79) - Comentário indicando o endpoint e a tarefa associada
    @PostMapping("/interesses") // Anotação que mapeia requisições HTTP POST para o caminho "/api/interesses"
    public ResponseEntity<InteresseResponse> registrarInteresse( // Método para registrar um novo interesse de adoção, retorna uma ResponseEntity com InteresseResponse
                                                                 @RequestBody @Valid RegistrarInteresseRequest req) { // @RequestBody: Indica que o corpo da requisição HTTP será mapeado para um objeto RegistrarInteresseRequest
        // @Valid: Ativa a validação do objeto 'req' conforme definido no DTO
        var resp = service.registrarInteresse(req.usuarioId(), req.petId(), req.mensagem()); // Chama o método 'registrarInteresse' no serviço, passando os dados da requisição
        return ResponseEntity.ok(resp); // Retorna uma resposta HTTP 200 OK com o objeto 'InteresseResponse' no corpo
    }

    // PUT /api/interesses/{id}/status (BUDDY-87) - Comentário indicando o endpoint e a tarefa associada
    @PutMapping("/interesses/{id}/status") // Anotação que mapeia requisições HTTP PUT para o caminho "/api/interesses/{id}/status"
    // {id} é uma variável de caminho que será substituída pelo ID real
    public ResponseEntity<InteresseResponse> atualizarStatus( // Método para atualizar o status de um interesse de adoção
                                                              @PathVariable Long id, // @PathVariable: Extrai o valor do 'id' da URL e o mapeia para o parâmetro 'id'
                                                              @RequestBody @Valid AtualizarStatusRequest req) { // @RequestBody e @Valid: Mapeiam e validam o corpo da requisição para um AtualizarStatusRequest
        var resp = service.atualizarStatus(id, req.status()); // Chama o método 'atualizarStatus' no serviço, passando o ID e o novo status
        return ResponseEntity.ok(resp); // Retorna uma resposta HTTP 200 OK com o objeto 'InteresseResponse' no corpo
    }

    // GET /api/usuarios/me/interesses (BUDDY-91) - Comentário indicando o endpoint e a tarefa associada
    // Trocar a origem do usuarioId pelo SecurityContext quando integrar autenticação - Observação importante sobre futura implementação
    @GetMapping("/usuarios/me/interesses") // Anotação que mapeia requisições HTTP GET para o caminho "/api/usuarios/me/interesses"
    public ResponseEntity<List<InteresseResponse>> listarMeusInteresses( // Método para listar interesses de adoção de um usuário, retorna uma lista de InteresseResponse
                                                                         @RequestParam Long usuarioId) { // @RequestParam: Extrai o valor do 'usuarioId' de um parâmetro de consulta na URL (ex: ?usuarioId=1)
        var resp = service.listarPorUsuario(usuarioId); // Chama o método 'listarPorUsuario' no serviço, passando o ID do usuário
        return ResponseEntity.ok(resp); // Retorna uma resposta HTTP 200 OK com a lista de 'InteresseResponse' no corpo
    }
}