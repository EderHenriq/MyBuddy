package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    //ENDPOINT DE CRIAÇÃO (CREATE) ---
    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.criarUsuario(usuario);
            return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    //ENDPOINTS DE LEITURA (READ) ---
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.buscarTodosUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) { // 11. @PathVariable extrai o valor do {id} da URL e o passa como parâmetro.
        Optional<Usuario> usuario = usuarioService.buscarUsuarioPorId(id);
        // Se o usuário for encontrado (isPresent), retorna status 200 (OK) e os dados do usuário.
        // Se não for encontrado, retorna status 404 (Not Found).
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //ENDPOINT DE ATUALIZAÇÃO (UPDATE) ---
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dadosUsuario) {
        try {
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, dadosUsuario);
            //Retorna status 200 (OK) e o usuário com os dados atualizados.
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //ENDPOINT DE DELEÇÃO (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletarUsuario(id);
            // Retorna status 204, que é o padrão para uma deleção bem-sucedida. Não há corpo na resposta.
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            // Se o usuário não for encontrado, retorna 404 (Not Found).
            return ResponseEntity.notFound().build();
        }
    }
}