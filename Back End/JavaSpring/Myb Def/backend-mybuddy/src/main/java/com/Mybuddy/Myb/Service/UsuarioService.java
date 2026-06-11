package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    public static final String KeycloakUserSyncService = null;
    private final UsuarioRepository usuarioRepository;

    public Usuario criarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ConflictException("O e-mail informado já está em uso.");
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> buscarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario atualizarUsuario(long id, Usuario dadosUsuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado."));

        usuarioExistente.setNome(dadosUsuario.getNome());
        usuarioExistente.setEmail(dadosUsuario.getEmail());
        usuarioExistente.setTelefone(dadosUsuario.getTelefone());
        usuarioExistente.setUrlAvatar(dadosUsuario.getUrlAvatar());

        return usuarioRepository.save(usuarioExistente);
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário com ID " + id + " não encontrado.");
        }
        usuarioRepository.deleteById(id);
    }
}