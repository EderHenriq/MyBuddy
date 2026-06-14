package com.Mybuddy.Myb.Scheduler;

import com.Mybuddy.Myb.Service.CampanhaDoacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampanhaSchedulerTest {

    @Mock
    private CampanhaDoacaoService campanhaService;

    @InjectMocks
    private CampanhaScheduler campanhaScheduler;

    @Test
    void expirarCampanhasVencidas_DeveChamarServicoParaExpirar() {
        when(campanhaService.expirarCampanhasAtivasExpiradas()).thenReturn(5);

        campanhaScheduler.expirarCampanhasVencidas();

        verify(campanhaService, times(1)).expirarCampanhasAtivasExpiradas();
    }

    @Test
    void expirarCampanhasVencidas_QuandoLancaExcecao_DeveTratarExcecao() {
        when(campanhaService.expirarCampanhasAtivasExpiradas()).thenThrow(new RuntimeException("Database error"));

        // Não deve propagar a exceção
        campanhaScheduler.expirarCampanhasVencidas();

        verify(campanhaService, times(1)).expirarCampanhasAtivasExpiradas();
    }
}
