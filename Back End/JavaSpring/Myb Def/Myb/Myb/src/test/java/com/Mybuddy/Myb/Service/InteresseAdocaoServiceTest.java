package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.InteresseResponse;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.InteresseAdoacaoRepository;
import com.Mybuddy.Myb.Repository.PetRepository;
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
@DisplayName("Testes Unitários - InteresseAdocaoService")
class InteresseAdocaoServiceTest {

    @Mock
    private InteresseAdoacaoRepository interesseRepo;

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private PetRepository petRepo;

    @InjectMocks
    private InteresseAdoacaoService interesseAdocaoService;

    private Usuario usuario;
    private Pet pet;
    private InteresseAdocao interesse;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");

        pet = new Pet();
        pet.setId(1L);
        pet.setNome("Rex");
        pet.setStatusAdocao(StatusAdocao.DISPONIVEL);

        interesse = new InteresseAdocao();
        interesse.setId(1L);
        interesse.setUsuario(usuario);
        interesse.setPet(pet);
        interesse.setStatus(StatusInteresse.PENDENTE);
        interesse.setMensagem("Gostaria de adotar");
    }

    @Test
    @DisplayName("Deve registrar interesse válido")
    void manifestarInteresse_InteresseValido_CriaComStatusPendente() {
        // Arrange
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(petRepo.findById(1L)).thenReturn(Optional.of(pet));
        when(interesseRepo.existsByUsuarioAndPet(usuario, pet)).thenReturn(false);
        when(interesseRepo.save(any(InteresseAdocao.class))).thenReturn(interesse);

        // Act
        InteresseResponse response = interesseAdocaoService.manifestarInteresse(1L, 1L, "Gostaria de adotar");

        // Assert
        assertNotNull(response);
        assertEquals(StatusInteresse.PENDENTE, response.status());
        verify(usuarioRepo).findById(1L);
        verify(petRepo).findById(1L);
        verify(interesseRepo).save(any(InteresseAdocao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pet já está adotado")
    void manifestarInteresse_PetAdotado_LancaExcecao() {
        // Arrange
        pet.setStatusAdocao(StatusAdocao.ADOTADO);
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(petRepo.findById(1L)).thenReturn(Optional.of(pet));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> interesseAdocaoService.manifestarInteresse(1L, 1L, "Mensagem"));

        assertTrue(exception.getMessage().contains("não está disponível"));
        verify(interesseRepo, never()).save(any(InteresseAdocao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário já manifestou interesse")
    void manifestarInteresse_InteresseDuplicado_LancaExcecao() {
        // Arrange
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(petRepo.findById(1L)).thenReturn(Optional.of(pet));
        when(interesseRepo.existsByUsuarioAndPet(usuario, pet)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> interesseAdocaoService.manifestarInteresse(1L, 1L, "Mensagem"));

        assertTrue(exception.getMessage().contains("já manifestou interesse"));
        verify(interesseRepo, never()).save(any(InteresseAdocao.class));
    }

    @Test
    @DisplayName("Deve aprovar interesse com sucesso")
    void atualizarStatus_AprovarInteresse_AtualizaParaAprovado() {
        // Arrange
        when(interesseRepo.findById(1L)).thenReturn(Optional.of(interesse));
        when(interesseRepo.save(any(InteresseAdocao.class))).thenAnswer(invocation -> {
            InteresseAdocao i = invocation.getArgument(0);
            i.setStatus(StatusInteresse.APROVADO);
            return i;
        });

        // Act
        InteresseResponse response = interesseAdocaoService.atualizarStatus(1L, StatusInteresse.APROVADO);

        // Assert
        assertNotNull(response);
        assertEquals(StatusInteresse.APROVADO, response.status());
        verify(interesseRepo).findById(1L);
        verify(interesseRepo).save(any(InteresseAdocao.class));
    }

    @Test
    @DisplayName("Deve rejeitar interesse com sucesso")
    void atualizarStatus_RejeitarInteresse_AtualizaParaRejeitado() {
        // Arrange
        when(interesseRepo.findById(1L)).thenReturn(Optional.of(interesse));
        when(interesseRepo.save(any(InteresseAdocao.class))).thenAnswer(invocation -> {
            InteresseAdocao i = invocation.getArgument(0);
            i.setStatus(StatusInteresse.REJEITADO);
            return i;
        });

        // Act
        InteresseResponse response = interesseAdocaoService.atualizarStatus(1L, StatusInteresse.REJEITADO);

        // Assert
        assertNotNull(response);
        assertEquals(StatusInteresse.REJEITADO, response.status());
        verify(interesseRepo).findById(1L);
        verify(interesseRepo).save(any(InteresseAdocao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar interesse inexistente")
    void atualizarStatus_InteresseInexistente_LancaExcecao() {
        // Arrange
        when(interesseRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> interesseAdocaoService.atualizarStatus(1L, StatusInteresse.APROVADO));

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(interesseRepo).findById(1L);
        verify(interesseRepo, never()).save(any(InteresseAdocao.class));
    }
}
