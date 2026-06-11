package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.EnderecoEntrega;
import com.Mybuddy.Myb.Model.InteresseAdocao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //ativa o mockito
class UsuarioServiceTest {

    @Mock //cria o repo 'falso'
    private UsuarioRepository usuarioRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private InteresseAdocaoRepository interesseAdocaoRepository;

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
                .isInstanceOf(ConflictException.class)
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
        Usuario dadosNovos = new Usuario("Eder Atualizado", "novo@mybuddy.com", "44988888888", "senha456");
        dadosNovos.setUrlAvatar("/uploads/novo-avatar.png");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.atualizarUsuario(1L, dadosNovos);

        assertThat(resultado.getNome()).isEqualTo("Eder Atualizado");
        assertThat(resultado.getEmail()).isEqualTo("novo@mybuddy.com");
        assertThat(resultado.getUrlAvatar()).isEqualTo("/uploads/novo-avatar.png");
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.atualizarUsuario(99L, usuario))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário com ID 99 não encontrado.");
    }

    // ===================== deletarUsuario =====================

    @Test
    void deveDeletarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(pedidoRepository.findByClienteId(1L)).thenReturn(Collections.emptyList());
        when(interesseAdocaoRepository.findByUsuarioId(1L)).thenReturn(Collections.emptyList());

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
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário com ID 99 não encontrado.");

        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    void deveDeletarUsuarioEAnonimizarPedidosEInteresses() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        
        Pedido pedido = new Pedido();
        pedido.setId(10L);
        pedido.setClienteId(1L);
        EnderecoEntrega endereco = EnderecoEntrega.builder()
                .logradouro("Rua A")
                .numero("123")
                .complemento("Ap 1")
                .bairro("Bairro")
                .cep("87000-000")
                .cidade("Maringá")
                .estado("PR")
                .build();
        pedido.setEnderecoEntrega(endereco);

        InteresseAdocao interesse = new InteresseAdocao();
        interesse.setId(20L);
        interesse.setUsuario(usuario);
        interesse.setCpfAdotante("12345678901");
        interesse.setMensagem("Quero adotar!");
        interesse.setMotivoAdocao("Companhia");

        when(pedidoRepository.findByClienteId(1L)).thenReturn(List.of(pedido));
        when(interesseAdocaoRepository.findByUsuarioId(1L)).thenReturn(List.of(interesse));

        // Act
        usuarioService.deletarUsuario(1L);

        // Assert
        // Pedido anonimizado
        assertThat(pedido.getEnderecoEntrega().getLogradouro()).isEqualTo("CLIENTE ANONIMIZADO");
        assertThat(pedido.getEnderecoEntrega().getCep()).isEqualTo("00000000");
        verify(pedidoRepository, times(1)).save(pedido);

        // Interesse anonimizado
        assertThat(interesse.getCpfAdotante()).isEqualTo("00000000000");
        assertThat(interesse.getMensagem()).isEqualTo("CONTEÚDO ANONIMIZADO PARA LGPD");
        assertThat(interesse.getUsuario()).isNull();
        verify(interesseAdocaoRepository, times(1)).save(interesse);

        // Usuário deletado
        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}