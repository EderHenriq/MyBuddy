package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize; // <<< ADICIONADO
import org.springframework.security.core.Authentication; // <<< ADICIONADO
import org.springframework.security.core.context.SecurityContextHolder; // <<< ADICIONADO
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl; // <<< ADICIONADO

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // <<< ADICIONADO: Apenas ADMIN pode criar usuários diretamente
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.criarUsuario(usuario);
            return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // <<< ADICIONADO: Apenas ADMIN pode listar todos os usuários
    public List<Usuario> listarUsuarios() {
        return usuarioService.buscarTodosUsuarios();
    }

    // --- NOVO ENDPOINT ADICIONADO: Para que qualquer usuário autenticado veja seu próprio perfil ---
    @GetMapping("/meu-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> getMeuPerfil() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        return usuarioService.buscarUsuarioPorId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <<< ADICIONADO: Apenas ADMIN pode buscar qualquer usuário por ID
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarUsuarioPorId(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    // Apenas ADMINS podem atualizar qualquer usuário, ou o próprio usuário pode atualizar seu perfil
    @PreAuthorize("hasRole('ADMIN') or (#id == authentication.principal.id)") // <<< ADICIONADO
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dadosUsuario) {
        try {
            // --- ALTERADO: Pode ser necessário passar o ID do usuário logado para o serviço ---
            // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            // Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, dadosUsuario, userDetails.getId());
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, dadosUsuario); // Mantendo sua chamada original por enquanto
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <<< ADICIONADO: Apenas ADMIN pode deletar qualquer usuário
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }
}