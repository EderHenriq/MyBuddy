package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.CampanhaDoacao;
import com.Mybuddy.Myb.Repository.mongo.CampanhaDoacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampanhaDoacaoServiceTest {

    @Mock
    private CampanhaDoacaoRepository repository;

    @InjectMocks
    private CampanhaDoacaoService service;

    private CampanhaDoacao campanhaAtiva;
    private CampanhaDoacao campanhaExpirada;

    @BeforeEach
    void setUp() {
        campanhaAtiva = CampanhaDoacao.builder()
                .id(1L)
                .titulo("Campanha Ativa")
                .descricao("Descrição")
                .meta(new BigDecimal("1000.00"))
                .arrecadado(BigDecimal.ZERO)
                .organizacaoId(10L)
                .categoria("GERAL")
                .dataExpiracao(LocalDateTime.now().plusDays(5))
                .status("ATIVA")
                .build();

        campanhaExpirada = CampanhaDoacao.builder()
                .id(2L)
                .titulo("Campanha Expirada")
                .descricao("Descrição")
                .meta(new BigDecimal("500.00"))
                .arrecadado(BigDecimal.ZERO)
                .organizacaoId(10L)
                .categoria("RACAO")
                .dataExpiracao(LocalDateTime.now().minusDays(1))
                .status("ATIVA")
                .build();
    }

    @Test
    void deveExpirarCampanhasAtivasExpiradasComSucesso() {
        when(repository.findByStatusAndDataExpiracaoBefore(eq("ATIVA"), any(LocalDateTime.class)))
                .thenReturn(List.of(campanhaExpirada));

        int result = service.expirarCampanhasAtivasExpiradas();

        assertThat(result).isEqualTo(1);
        assertThat(campanhaExpirada.getStatus()).isEqualTo("ENCERRADA");
        verify(repository, times(1)).save(campanhaExpirada);
    }

    @Test
    void deveRetornarZeroSeNaoHouverCampanhasExpiradas() {
        when(repository.findByStatusAndDataExpiracaoBefore(eq("ATIVA"), any(LocalDateTime.class)))
                .thenReturn(List.of());

        int result = service.expirarCampanhasAtivasExpiradas();

        assertThat(result).isZero();
        verify(repository, never()).save(any());
    }

    @Test
    void deveListarTodasAsCampanhas() {
        when(repository.findAll()).thenReturn(List.of(campanhaAtiva, campanhaExpirada));

        List<CampanhaDoacao> result = service.listarTodas();

        assertThat(result).hasSize(2);
        verify(repository, times(1)).findAll();
    }

    @Test
    void deveCriarCampanhaComSucesso() {
        when(repository.save(any(CampanhaDoacao.class))).thenReturn(campanhaAtiva);

        CampanhaDoacao result = service.criar(campanhaAtiva);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("ATIVA");
        assertThat(result.getArrecadado()).isEqualTo(BigDecimal.ZERO);
        verify(repository, times(1)).save(campanhaAtiva);
    }

    @Test
    void deveAtualizarCampanhaComSucesso() {
        CampanhaDoacao dadosNovos = CampanhaDoacao.builder()
                .titulo("Novo Titulo")
                .descricao("Nova Descricao")
                .meta(new BigDecimal("2000.00"))
                .categoria("TRATAMENTO")
                .dataExpiracao(LocalDateTime.now().plusDays(10))
                .status("META_ATINGIDA")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(campanhaAtiva));
        when(repository.save(any(CampanhaDoacao.class))).thenReturn(campanhaAtiva);

        CampanhaDoacao result = service.atualizar(1L, dadosNovos);

        assertThat(result).isNotNull();
        assertThat(campanhaAtiva.getTitulo()).isEqualTo("Novo Titulo");
        assertThat(campanhaAtiva.getStatus()).isEqualTo("META_ATINGIDA");
        verify(repository, times(1)).save(campanhaAtiva);
    }

    @Test
    void deveLancarExcecaoAoAtualizarCampanhaInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(99L, campanhaAtiva))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Campanha não encontrada: 99");
    }
}
