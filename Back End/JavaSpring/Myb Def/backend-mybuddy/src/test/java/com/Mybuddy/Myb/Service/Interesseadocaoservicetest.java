package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.InteresseResponse;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.InteresseAdocaoRepository;
import com.Mybuddy.Myb.Repository.PetRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InteresseAdocaoServiceTest {

    @Mock
    private InteresseAdocaoRepository interesseRepo;

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private PetRepository petRepo;

    @InjectMocks
    private InteresseAdocaoService interesseAdocaoService;

    private Usuario usuario;
    private Pet pet;
    private InteresseAdocao interesse;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");

        pet = new Pet();
        pet.setId(1L);
        pet.setNome("Rex");
        pet.setStatusAdocao(StatusAdocao.DISPONIVEL);

        interesse = new InteresseAdocao();
        interesse.setId(1L);
        interesse.setUsuario(usuario);
        interesse.setPet(pet);
        interesse.setMensagem("Quero adotar!");
        interesse.setStatus(StatusInteresse.PENDENTE);
    }

    // ===================== MANIFESTAR INTERESSE =====================

    @Test
    void deveManifestrarInteresseComSucesso() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(petRepo.findById(1L)).thenReturn(Optional.of(pet));
        when(interesseRepo.existsByUsuarioAndPet(usuario, pet)).thenReturn(false);
        when(interesseRepo.save(any(InteresseAdocao.class))).thenReturn(interesse);

        InteresseResponse result = interesseAdocaoService.manifestarInteresse(1L, 1L, "Quero adotar!");

        assertThat(result).isNotNull();
        verify(interesseRepo, times(1)).save(any(InteresseAdocao.class));
    }

    @Test
    void deveLancarExcecaoAoManifestarInteresseComUsuarioInexistente() {
        when(usuarioRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interesseAdocaoService.manifestarInteresse(99L, 1L, "mensagem"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuário não encontrado: 99");
    }

    @Test
    void deveLancarExcecaoAoManifestarInteresseComPetInexistente() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(petRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interesseAdocaoService.manifestarInteresse(1L, 99L, "mensagem"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pet não encontrado: 99");
    }

    @Test
    void deveLancarExcecaoAoManifestarInteresseComPetIndisponivel() {
        pet.setStatusAdocao(StatusAdocao.ADOTADO);

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(petRepo.findById(1L)).thenReturn(Optional.of(pet));

        assertThatThrownBy(() -> interesseAdocaoService.manifestarInteresse(1L, 1L, "mensagem"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Pet não está disponível para adoção");
    }

    @Test
    void deveLancarExcecaoAoManifestarInteresseDuplicado() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(petRepo.findById(1L)).thenReturn(Optional.of(pet));
        when(interesseRepo.existsByUsuarioAndPet(usuario, pet)).thenReturn(true);

        assertThatThrownBy(() -> interesseAdocaoService.manifestarInteresse(1L, 1L, "mensagem"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Você já manifestou interesse neste pet");
    }

    // ===================== ATUALIZAR STATUS =====================

    @Test
    void deveAtualizarStatusInteresseComSucesso() {
        when(interesseRepo.findById(1L)).thenReturn(Optional.of(interesse));
        when(interesseRepo.save(any(InteresseAdocao.class))).thenReturn(interesse);

        InteresseResponse result = interesseAdocaoService.atualizarStatus(1L, StatusInteresse.APROVADO);

        assertThat(result).isNotNull();
        verify(interesseRepo, times(1)).save(any(InteresseAdocao.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarStatusDeInteresseInexistente() {
        when(interesseRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interesseAdocaoService.atualizarStatus(99L, StatusInteresse.APROVADO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Interesse não encontrado: 99");
    }

    // ===================== LISTAR =====================

    @Test
    void deveListarInteressesPorUsuario() {
        when(interesseRepo.findByUsuarioIdWithFetch(1L)).thenReturn(List.of(interesse));

        List<InteresseResponse> result = interesseAdocaoService.listarPorUsuario(1L);

        assertThat(result).hasSize(1);
        verify(interesseRepo, times(1)).findByUsuarioIdWithFetch(1L);
    }

    @Test
    void deveListarTodosInteresses() {
        when(interesseRepo.findAllWithFetch()).thenReturn(List.of(interesse));

        List<InteresseResponse> result = interesseAdocaoService.listarTodos();

        assertThat(result).hasSize(1);
        verify(interesseRepo, times(1)).findAllWithFetch();
    }

    @Test
    void deveListarInteressesPorOrganizacao() {
        when(interesseRepo.findByPetOrganizacaoIdWithFetch(1L)).thenReturn(List.of(interesse));

        List<InteresseResponse> result = interesseAdocaoService.listarInteressesPorOrganizacao(1L);

        assertThat(result).hasSize(1);
        verify(interesseRepo, times(1)).findByPetOrganizacaoIdWithFetch(1L);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverInteressesPorUsuario() {
        when(interesseRepo.findByUsuarioIdWithFetch(99L)).thenReturn(List.of());

        List<InteresseResponse> result = interesseAdocaoService.listarPorUsuario(99L);

        assertThat(result).isEmpty();
    }
}