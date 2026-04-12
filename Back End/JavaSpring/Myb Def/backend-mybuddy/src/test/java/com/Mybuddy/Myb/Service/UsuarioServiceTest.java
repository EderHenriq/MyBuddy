package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //ativa o mockito
class UsuarioServiceTest {

    @Mock //cria o repo 'falso'
    private UsuarioRepository usuarioRepository;

    @InjectMocks // cria o service real, e coloca o mock dentro dele
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach // prepara os dados antes de começar o teste
    void setUp() {
        // Arrange global — usuário base pra todos os testes
        usuario = new Usuario("Eder", "eder@mybuddy.com", "44999999999", "senha123");
        usuario.setId(1L);
    }

    // ===================== criarUsuario =====================

    @Test
    void deveCriarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.empty()); //esse when ensina o mock oq retornar
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.criarUsuario(usuario);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("eder@mybuddy.com");
        verify(usuarioRepository, times(1)).save(usuario); //o verify, verifica se um metodo foi chamado,nesse caso, verifica se foi chamado UMA vez
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Arrange
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.criarUsuario(usuario))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("O e-mail informado já está em uso.");

        verify(usuarioRepository, never()).save(any());
    }

    // ===================== buscarTodosUsuarios =====================

    @Test
    void deveRetornarListaDeUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        // Act
        List<Usuario> resultado = usuarioService.buscarTodosUsuarios();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Eder");
    }

    // ===================== buscarUsuarioPorId =====================

    @Test
    void deveRetornarUsuarioPorId() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.buscarUsuarioPorId(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    void eeveRetornarVazioQuandoUsuarioNaoEncontrado() {
        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.buscarUsuarioPorId(99L);

        // Assert
        assertThat(resultado).isEmpty();
    }

    // ===================== atualizarUsuario =====================

    @Test
    void deveAtualizarUsuarioComSucesso() {
        // Arrange
        Usuario dadosNovos = new Usuario("Eder Atualizado", "novo@mybuddy.com", "44988888888", "senha456");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.atualizarUsuario(1L, dadosNovos);

        // Assert
        assertThat(resultado.getNome()).isEqualTo("Eder Atualizado");
        assertThat(resultado.getEmail()).isEqualTo("novo@mybuddy.com");
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.atualizarUsuario(99L, usuario))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Usuário com ID 99 não encontrado.");
    }

    // ===================== deletarUsuario =====================

    @Test
    void deveDeletarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        usuarioService.deletarUsuario(1L);

        // Assert
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.deletarUsuario(99L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Usuário com ID 99 não encontrado.");

        verify(usuarioRepository, never()).deleteById(any());
    }
}