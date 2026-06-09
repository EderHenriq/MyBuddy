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
import java.util.Objects;

@RestController
@RequestMapping("/api/donations/stats")
@RequiredArgsConstructor
public class DonationStatsController {

    private final PetRepository petRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping
    public ResponseEntity<DonationStatsResponseDTO> getStats() {
        // 1. Contar pets adotados (salvos)
        long petsSalvos = petRepository.findAll().stream()
                .filter(p -> p.getStatusAdocao() == StatusAdocao.ADOTADO)
                .count();

        // 2. Contar ONGs parceiras
        long ongsParceiras = organizacaoRepository.count();

        // 3. Obter total arrecadado das doações/pagamentos aprovados
        BigDecimal totalArrecadado = paymentRepository.findByStatus(PaymentStatus.APPROVED).stream()
                .map(p -> Objects.requireNonNullElse(p.getAmount(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Obter contagem de doadores ativos (usuários distintos com pagamentos aprovados)
        long doadoresAtivos = paymentRepository.findByStatus(PaymentStatus.APPROVED).stream()
                .map(p -> p.getUsuarioId())
                .filter(Objects::nonNull)
                .distinct()
                .count();

        return ResponseEntity.ok(new DonationStatsResponseDTO(
                petsSalvos,
                ongsParceiras,
                totalArrecadado,
                doadoresAtivos
        ));
    }
}
