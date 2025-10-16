package com.Mybuddy.Myb.Controller; // Declara o pacote onde esta classe está localizada

import com.Mybuddy.Myb.Dto.AtualizarStatusRequest; // Importa o DTO (Data Transfer Object) para requisições de atualização de status
import com.Mybuddy.Myb.Dto.InteresseResponse; // Importa o DTO para respostas de interesse de adoção
import com.Mybuddy.Myb.Dto.RegistrarInteresseRequest; // Importa o DTO para requisições de registro de interesse
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl; // Importa a classe de detalhes do usuário autenticado para extrair informações do JWT
import com.Mybuddy.Myb.Service.InteresseAdoacaoService; // Importa a classe de serviço que contém a lógica de negócio
import jakarta.validation.Valid; // Importa a anotação para validação de objetos DTO
import org.springframework.http.HttpStatus; // Importa a classe para definir códigos de status HTTP
import org.springframework.http.ResponseEntity; // Importa a classe para construir respostas HTTP
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Importa a interface Authentication para acessar o contexto de segurança
import org.springframework.web.bind.annotation.*; // Importa todas as anotações do Spring Web para REST controllers

import java.util.List; // Importa a interface List para coleções de objetos

@PreAuthorize("hasRole('ADMIN')")//Permite Que ADM tenha acesso a permissão de listar todos conforme o TOKEN JWT e o
//Role locaziado no localStorage no navegador, isso se aplica a todas as menções (ATRIBUIR A ONG TAMBÉM)
@RestController // Anotação que indica que esta classe é um controlador REST, combinando @Controller e @ResponseBody
@RequestMapping("/api") // Anotação que mapeia todas as requisições que começam com "/api" para este controlador
public class InteresseAdoacaoController { // Declara a classe do controlador

    private final InteresseAdoacaoService service; // Declara uma instância do serviço de interesse de adoção, que é final (não pode ser reatribuída)

    // Construtor do controlador. O Spring injeta automaticamente uma instância de InteresseAdoacaoService aqui.
    public InteresseAdoacaoController(InteresseAdoacaoService service) {
        this.service = service; // Atribui a instância do serviço injetada à variável 'service'
    }

    // POST /api/interesses (BUDDY-77) - Comentário indicando o endpoint e a tarefa associada
    // Este método permite que um usuário autenticado manifeste interesse em adotar um pet disponível
    @PostMapping("/interesses") // Anotação que mapeia requisições HTTP POST para o caminho "/api/interesses"
    public ResponseEntity<InteresseResponse> manifestarInteresse( // Método para manifestar interesse em adoção, retorna uma ResponseEntity com InteresseResponse
                                                                  @RequestBody @Valid RegistrarInteresseRequest req, // @RequestBody: Indica que o corpo da requisição HTTP será mapeado para um objeto RegistrarInteresseRequest
                                                                  // @Valid: Ativa a validação do objeto 'req' conforme definido no DTO
                                                                  Authentication authentication) { // Authentication: Objeto injetado pelo Spring Security contendo informações do usuário autenticado

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal(); // Extrai os detalhes do usuário autenticado do contexto de segurança
        Long usuarioId = userDetails.getId(); // Obtém o ID do usuário autenticado a partir dos detalhes extraídos

        var resp = service.manifestarInteresse(usuarioId, req.petId(), req.mensagem()); // Chama o método 'manifestarInteresse' no serviço, passando o ID do usuário autenticado, ID do pet e mensagem
        return ResponseEntity.status(HttpStatus.CREATED).body(resp); // Retorna uma resposta HTTP 201 CREATED com o objeto 'InteresseResponse' no corpo
    }

    // PUT /api/interesses/{id}/status (BUDDY-87) - Comentário indicando o endpoint e a tarefa associada
    // Este método permite que usuários autorizados (ex: ONG, ADMIN) atualizem o status de um interesse de adoção

    @PutMapping("/interesses/{id}/status") // Anotação que mapeia requisições HTTP PUT para o caminho "/api/interesses/{id}/status"
    // {id} é uma variável de caminho que será substituída pelo ID real do interesse
    public ResponseEntity<InteresseResponse> atualizarStatus( // Método para atualizar o status de um interesse de adoção
                                                              @PathVariable Long id, // @PathVariable: Extrai o valor do 'id' da URL e o mapeia para o parâmetro 'id'
                                                              @RequestBody @Valid AtualizarStatusRequest req) { // @RequestBody e @Valid: Mapeiam e validam o corpo da requisição para um AtualizarStatusRequest
        var resp = service.atualizarStatus(id, req.status()); // Chama o método 'atualizarStatus' no serviço, passando o ID do interesse e o novo status
        return ResponseEntity.ok(resp); // Retorna uma resposta HTTP 200 OK com o objeto 'InteresseResponse' atualizado no corpo
    }

    // GET /api/usuarios/me/interesses (BUDDY-91) - Comentário indicando o endpoint e a tarefa associada
    // Este método permite que um usuário autenticado liste todos os seus interesses de adoção registrados
    @PreAuthorize("hasRole('ADMIN')")//Permite Que ADM tenha acesso a permissão de listar todos conforme o TOKEN JWT e o
//Role locaziado no localStorage no navegador, isso se aplica a todas as menções (ATRIBUIR A ONG TAMBÉM)
    @GetMapping("/usuarios/me/interesses") // Anotação que mapeia requisições HTTP GET para o caminho "/api/usuarios/me/interesses"
    public ResponseEntity<List<InteresseResponse>> listarMeusInteresses( // Método para listar interesses de adoção do usuário autenticado, retorna uma lista de InteresseResponse
                                                                         @RequestParam Long usuarioId) { // @RequestParam: Extrai o valor do 'usuarioId' de um parâmetro de consulta na URL (ex: ?usuarioId=1)
        // TODO: Trocar @RequestParam usuarioId por Authentication após integrar autenticação JWT completamente
        var resp = service.listarPorUsuario(usuarioId); // Chama o método 'listarPorUsuario' no serviço, passando o ID do usuário
        return ResponseEntity.ok(resp); // Retorna uma resposta HTTP 200 OK com a lista de 'InteresseResponse' no corpo
    }

    @GetMapping("/interesses")
    public ResponseEntity<List<InteresseResponse>> listarTodos() {
        var resp = service.listarTodos();
        return ResponseEntity.ok(resp);
    }

}
