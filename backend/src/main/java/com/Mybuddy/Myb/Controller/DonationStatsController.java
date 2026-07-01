package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.DTO.DonationStatsResponseDTO;
import com.Mybuddy.Myb.Model.PaymentStatus;
import com.Mybuddy.Myb.Model.StatusAdocao;
import com.Mybuddy.Myb.Repository.jpa.PaymentRepository;
import com.Mybuddy.Myb.Repository.mongo.OrganizacaoRepository;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/donations/stats")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class DonationStatsController {

    private final PetRepository petRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping
    public ResponseEntity<DonationStatsResponseDTO> getStats() {
        long petsSalvos = petRepository.countByStatusAdocao(StatusAdocao.ADOTADO);
        long ongsParceiras = organizacaoRepository.count();
        BigDecimal totalArrecadado = paymentRepository.sumAmountByStatus(PaymentStatus.APPROVED);
        long doadoresAtivos = paymentRepository.countDistinctUsuarioIdByStatus(PaymentStatus.APPROVED);

        return ResponseEntity.ok(new DonationStatsResponseDTO(
                petsSalvos,
                ongsParceiras,
                totalArrecadado,
                doadoresAtivos
        ));
    }
}
