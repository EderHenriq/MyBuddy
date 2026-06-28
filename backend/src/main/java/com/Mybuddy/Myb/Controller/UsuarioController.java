package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.FotoPetService;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final KeycloakUserSyncService keycloakUserSyncService;
    private final FotoPetService fotoPetService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.criarUsuario(usuario), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listarUsuarios() {
        return usuarioService.buscarTodosUsuarios();
    }

    @GetMapping("/meu-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> getMeuPerfil(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/meu-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> atualizarMeuPerfil(@AuthenticationPrincipal Jwt jwt, @RequestBody Usuario dadosUsuario) {
        Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
        usuario.setNome(dadosUsuario.getNome());
        usuario.setTelefone(dadosUsuario.getTelefone());
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(usuario.getId(), usuario);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PostMapping("/meu-perfil/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor selecione um arquivo.");
        }
        try {
            String fileName = fotoPetService.storeFile(file);
            Usuario usuario = keycloakUserSyncService.syncUsuario(jwt);
            usuario.setUrlAvatar("/uploads/" + fileName);
            usuarioService.atualizarUsuario(usuario.getId(), usuario);
            return ResponseEntity.ok("/uploads/" + fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar avatar: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dadosUsuario) {
        return ResponseEntity.ok(usuarioService.atualizarUsuario(id, dadosUsuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletarUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuarioLogado = keycloakUserSyncService.syncUsuario(jwt);
        boolean isAdmin = usuarioLogado.getRoles().stream()
                .anyMatch(r -> r.getName() == com.Mybuddy.Myb.Security.ERole.ROLE_ADMIN);

        if (!isAdmin && !usuarioLogado.getId().equals(id)) {
            throw new org.springframework.security.authorization.AuthorizationDeniedException("Você não tem permissão para deletar este usuário.");
        }

        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}