package com.Mybuddy.Myb.Service; // Declara o pacote onde esta classe de serviço está localizada.

import com.Mybuddy.Myb.Model.Usuario; // Importa a entidade Usuario, que representa um usuário no sistema.
import com.Mybuddy.Myb.Repository.UsuarioRepository; // Importa o repositório para a entidade Usuario.
import org.springframework.beans.factory.annotation.Autowired; // Importa a anotação para injeção de dependência.
import org.springframework.stereotype.Service; // Importa a anotação @Service do Spring.

import java.util.List; // Importa a interface List para lidar com coleções de objetos.
import java.util.Optional; // Importa a classe Optional para lidar com resultados que podem estar ausentes.

// Anotação @Service do Spring, que marca esta classe como um componente de serviço.
// Classes de serviço contêm a lógica de negócio principal da aplicação e atuam como intermediárias
// entre os controladores e as camadas de persistência (repositórios).
@Service
public class UsuarioService { // Declara a classe de serviço para Usuários.

    // Anotação que realiza a injeção de dependência. O Spring fornecerá automaticamente uma instância de UsuarioRepository.
    @Autowired
    private UsuarioRepository usuarioRepository; // Declara uma variável para o serviço de usuário.

    // Método para criar um novo usuário.
    public Usuario criarUsuario (Usuario usuario){

        // Verifica se já existe um usuário com este e-mail no banco de dados.
        // O método 'findByEmail' retorna um Optional, e 'isPresent()' verifica se ele contém um valor.
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            // Se o e-mail já existir, lança uma exceção para impedir a criação de um usuário duplicado.
            throw new IllegalStateException("O e-mail informado já está em uso.");
        }
        // Se o e-mail for único, salva o objeto Usuario no banco de dados através do repositório e retorna a entidade salva.
        return usuarioRepository.save(usuario);
    }

    // Método para listar todos os usuários cadastrados.
    public List<Usuario> buscarTodosUsuarios() {
        // Retorna todos os usuários encontrados pelo repositório.
        return usuarioRepository.findAll();
    }

    // Método para buscar um único usuário pelo seu ID.
    public Optional<Usuario> buscarUsuarioPorId (Long id) {
        // Retorna um Optional contendo o usuário se encontrado, ou um Optional vazio caso contrário.
        return usuarioRepository.findById(id);
    }

    // Método para atualizar os dados de um usuário existente.
    public Usuario atualizarUsuario(long id, Usuario dadosUsuario) {

        // Busca o usuário existente pelo ID. Se não for encontrado, lança uma exceção IllegalStateException.
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Usuário com ID " + id + " não encontrado."));

        // Atualiza os campos do usuário existente com os novos dados fornecidos no objeto 'dadosUsuario'.
        usuarioExistente.setNome(dadosUsuario.getNome());
        usuarioExistente.setEmail(dadosUsuario.getEmail());
        usuarioExistente.setTelefone(dadosUsuario.getTelefone());

        // Salva o usuário existente (com os dados atualizados) no banco de dados e o retorna.
        return usuarioRepository.save(usuarioExistente);
    }

    // Método para deletar um usuário pelo seu ID.
    public void deletarUsuario (Long id) {
        // Verifica se o usuário com o ID fornecido realmente existe.
        if (!usuarioRepository.existsById(id)) {
            // Se não existir, lança uma exceção.
            throw new IllegalStateException("Usuário com ID " + id + " não encontrado.");
        }
        // Se o usuário existir, deleta o usuário do banco de dados.
        usuarioRepository.deleteById(id);
    }
}