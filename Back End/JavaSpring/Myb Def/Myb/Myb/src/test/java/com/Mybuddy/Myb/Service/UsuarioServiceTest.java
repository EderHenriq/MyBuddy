package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setTelefone("11999999999");
        usuario.setPassword("senha123");
    }

    @Test
    @DisplayName("Deve criar usuário com email único")
    void criarUsuario_EmailUnico_CriaComSucesso() {
        // Arrange
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.criarUsuario(usuario);

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@test.com", resultado.getEmail());
        verify(usuarioRepository).findByEmail("joao@test.com");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void criarUsuario_EmailDuplicado_LancaExcecao() {
        // Arrange
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> usuarioService.criarUsuario(usuario));

        assertTrue(exception.getMessage().contains("e-mail informado já está em uso"));
        verify(usuarioRepository).findByEmail("joao@test.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar usuário existente")
    void atualizarUsuario_UsuarioExistente_AtualizaDados() {
        // Arrange
        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setNome("João Silva Atualizado");
        usuarioAtualizado.setEmail("joao.novo@test.com");
        usuarioAtualizado.setTelefone("11988888888");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.atualizarUsuario(1L, usuarioAtualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", resultado.getNome());
        assertEquals("joao.novo@test.com", resultado.getEmail());
        assertEquals("11988888888", resultado.getTelefone());
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
    void atualizarUsuario_UsuarioInexistente_LancaExcecao() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> usuarioService.atualizarUsuario(1L, usuario));

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar usuário existente")
    void deletarUsuario_UsuarioExistente_DeletaComSucesso() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> usuarioService.deletarUsuario(1L));

        // Assert
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void deletarUsuario_UsuarioInexistente_LancaExcecao() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> usuarioService.deletarUsuario(1L));

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository, never()).deleteById(anyLong());
    }
}
