package com.Mybuddy.Myb.Controller; // Declara o pacote onde esta classe está localizada

import com.Mybuddy.Myb.Dto.AtualizarStatusRequest; // DTO para requisições de atualização de status
import com.Mybuddy.Myb.Dto.InteresseResponse; // DTO para respostas de interesse de adoção
import com.Mybuddy.Myb.Dto.RegistrarInteresseRequest; // DTO para requisições de registro de interesse
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl; // Detalhes do usuário autenticado extraídos do JWT
import com.Mybuddy.Myb.Service.InteresseAdoacaoService; // Serviço com a lógica de negócio
import jakarta.validation.Valid; // Validação de DTOs
import org.springframework.http.HttpStatus; // Códigos HTTP
import org.springframework.http.ResponseEntity; // Respostas HTTP
import org.springframework.security.access.prepost.PreAuthorize; // Segurança por método
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Injeta o usuário autenticado
import org.springframework.web.bind.annotation.*; // Anotações REST

import java.util.List; // Coleções

@RestController // Controlador REST
@RequestMapping("/api") // Prefixo base dos endpoints
public class InteresseAdoacaoController { // Controlador de interesses de adoção

    private final InteresseAdoacaoService service; // Serviço injetado

    // Construtor com injeção do serviço
    public InteresseAdoacaoController(InteresseAdoacaoService service) {
        this.service = service; // Atribui a instância do serviço
    }

    // POST /api/interesses (BUDDY-77)
    // Permite que um usuário autenticado manifeste interesse em adotar um pet disponível
    @PostMapping("/interesses") // Mapeia requisições HTTP POST para "/api/interesses"
    @PreAuthorize("isAuthenticated()") // Exige autenticação para manifestar interesse
    public ResponseEntity<InteresseResponse> manifestarInteresse( // Retorna InteresseResponse com status 201
                                                                  @RequestBody @Valid RegistrarInteresseRequest req, // Corpo da requisição com validação
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails // Usuário autenticado extraído do contexto de segurança
    ) {
        Long usuarioId = userDetails.getId(); // Obtém o ID do usuário autenticado
        var resp = service.manifestarInteresse(usuarioId, req.petId(), req.mensagem()); // Chama o serviço com o ID do usuário, ID do pet e mensagem
        return ResponseEntity.status(HttpStatus.CREATED).body(resp); // Retorna 201 CREATED com o corpo da resposta
    }

    // PUT /api/interesses/{id}/status (BUDDY-87)
    // Permite que usuários autorizados (ex.: ONG, ADMIN) atualizem o status de um interesse de adoção
    @PutMapping("/interesses/{id}/status") // Mapeia requisições HTTP PUT para "/api/interesses/{id}/status"
    @PreAuthorize("hasAnyRole('ADMIN','ONG')") // Restringe a atualização de status a ADMIN e ONG
    public ResponseEntity<InteresseResponse> atualizarStatus( // Retorna InteresseResponse atualizado
                                                              @PathVariable Long id, // Extrai o ID do interesse da URL
                                                              @RequestBody @Valid AtualizarStatusRequest req // Corpo da requisição com o novo status e validação
    ) {
        var resp = service.atualizarStatus(id, req.status()); // Atualiza o status via serviço
        return ResponseEntity.ok(resp); // Retorna 200 OK com o objeto atualizado
    }

    // GET /api/usuarios/me/interesses (BUDDY-91)
    // Lista todos os interesses de adoção do usuário autenticado
    @GetMapping("/usuarios/me/interesses") // Mapeia requisições HTTP GET para "/api/usuarios/me/interesses"
    @PreAuthorize("isAuthenticated()") // Exige autenticação para listar os próprios interesses
    public ResponseEntity<List<InteresseResponse>> listarMeusInteresses( // Retorna a lista de InteresseResponse
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails // Injeta o usuário autenticado
    ) {
        Long usuarioId = userDetails.getId(); // Obtém o ID do usuário autenticado
        var resp = service.listarPorUsuario(usuarioId); // Busca os interesses do próprio usuário
        return ResponseEntity.ok(resp); // Retorna 200 OK com a lista de interesses
    }

    // GET /api/interesses
    // Lista todos os interesses (restrito a administradores)
    @GetMapping("/interesses") // Mapeia requisições HTTP GET para "/api/interesses"
    @PreAuthorize("hasRole('ADMIN')") // Restringe o acesso ao perfil ADMIN
    public ResponseEntity<List<InteresseResponse>> listarTodos() { // Retorna a lista completa de interesses
        var resp = service.listarTodos(); // Busca todos os interesses via serviço
        return ResponseEntity.ok(resp); // Retorna 200 OK com a lista completa
    }
}
