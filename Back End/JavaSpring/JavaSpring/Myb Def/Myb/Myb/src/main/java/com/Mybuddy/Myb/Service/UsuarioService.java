package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    //Chama o Repository
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Metodo de criar usuario
    public Usuario criarUsuario (Usuario usuario){

        // verifica se já existe um usuário com este e-mail.
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalStateException("O e-mail informado já está em uso.");
        }
        //Salva o usuario
        return usuarioRepository.save(usuario);
    }

    //Lista todos os usuarios
    public List<Usuario> buscarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    //Busca usuario por id
    public Optional<Usuario> buscarUsuarioPorId (Long id) {
        return usuarioRepository.findById(id);
    }

    // Atualiza usuario existente
    public Usuario atualizarUsuario(long id, Usuario dadosUsuario) {

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Usuário com ID " + id + " não encontrado."));


        usuarioExistente.setNome(dadosUsuario.getNome());
        usuarioExistente.setEmail(dadosUsuario.getEmail());
        usuarioExistente.setTelefone(dadosUsuario.getTelefone());

        return usuarioRepository.save(usuarioExistente);
    }

    //Deleta o usuario
    public void deletarUsuario (Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalStateException("Usuário com ID " + id + " não encontrado.");}
        usuarioRepository.deleteById(id);
    }








}
