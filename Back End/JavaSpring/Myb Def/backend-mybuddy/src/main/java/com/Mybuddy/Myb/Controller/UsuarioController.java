package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
import com.Mybuddy.Myb.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        try {
            return new ResponseEntity<>(usuarioService.criarUsuario(usuario), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listarUsuarios() {
        return usuarioService.buscarTodosUsuarios();
    }

    @GetMapping("/meu-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> getMeuPerfil() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return usuarioService.buscarUsuarioPorId(userDetails.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (#id == authentication.principal.id)")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dadosUsuario) {
        try {
            return ResponseEntity.ok(usuarioService.atualizarUsuario(id, dadosUsuario));
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }
}