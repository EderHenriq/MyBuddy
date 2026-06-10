package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.DonationStatsResponseDTO;
import com.Mybuddy.Myb.Model.PaymentStatus;
import com.Mybuddy.Myb.Model.StatusAdocao;
import com.Mybuddy.Myb.Repository.jpa.PaymentRepository;
import com.Mybuddy.Myb.Repository.mongo.OrganizacaoRepository;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationStatsControllerTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private OrganizacaoRepository organizacaoRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private DonationStatsController controller;

    @Test
    void deveRetornarEstatisticasComSucesso() {
        when(petRepository.countByStatusAdocao(StatusAdocao.ADOTADO)).thenReturn(15L);
        when(organizacaoRepository.count()).thenReturn(5L);
        when(paymentRepository.sumAmountByStatus(PaymentStatus.APPROVED)).thenReturn(new BigDecimal("1500.00"));
        when(paymentRepository.countDistinctUsuarioIdByStatus(PaymentStatus.APPROVED)).thenReturn(10L);

        ResponseEntity<DonationStatsResponseDTO> response = controller.getStats();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().petsSalvos()).isEqualTo(15L);
        assertThat(response.getBody().ongsParceiras()).isEqualTo(5L);
        assertThat(response.getBody().totalArrecadado()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(response.getBody().doadoresAtivos()).isEqualTo(10L);
    }
}
