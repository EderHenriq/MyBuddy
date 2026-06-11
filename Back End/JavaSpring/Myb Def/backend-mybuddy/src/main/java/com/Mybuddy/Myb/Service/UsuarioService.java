package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.EnderecoEntrega;
import com.Mybuddy.Myb.Model.InteresseAdocao;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    public static final String KeycloakUserSyncService = null;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final InteresseAdocaoRepository interesseAdocaoRepository;

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

        // 1. Anonimizar dados pessoais nos pedidos históricos do usuário
        List<Pedido> pedidos = pedidoRepository.findByClienteId(id);
        for (Pedido pedido : pedidos) {
            EnderecoEntrega endereco = pedido.getEnderecoEntrega();
            if (endereco != null) {
                endereco.setLogradouro("CLIENTE ANONIMIZADO");
                endereco.setNumero("ANONIMIZADO");
                endereco.setComplemento("ANONIMIZADO");
                endereco.setBairro("ANONIMIZADO");
                endereco.setCep("00000000");
                endereco.setCidade("ANONIMIZADO");
                endereco.setEstado("AN");
            }
            pedidoRepository.save(pedido);
        }

        // 2. Anonimizar dados pessoais nas triagens de adoção
        List<InteresseAdocao> interesses = interesseAdocaoRepository.findByUsuarioId(id);
        for (InteresseAdocao interesse : interesses) {
            interesse.setCpfAdotante("00000000000");
            interesse.setMensagem("CONTEÚDO ANONIMIZADO PARA LGPD");
            interesse.setMotivoAdocao("CONTEÚDO ANONIMIZADO PARA LGPD");
            interesse.setUsuario(null);
            interesseAdocaoRepository.save(interesse);
        }

        // 3. Deletar o usuário do banco de dados (MongoDB)
        usuarioRepository.deleteById(id);
    }
}