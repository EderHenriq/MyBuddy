package com.Mybuddy.Myb.Scheduler;

import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.StatusPedido;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PedidoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PedidoScheduler.class);

    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    /**
     * Executa a cada 10 minutos (600.000 ms) verificando pedidos PENDENTES
     * criados há mais de 30 minutos para cancelá-los automaticamente.
     */
    @Scheduled(fixedRate = 600000)
    public void cancelarPedidosPendentesExpirados() {
        logger.info("Iniciando verificação de pedidos pendentes expirados...");

        LocalDateTime limite = LocalDateTime.now().minusMinutes(30);
        List<Pedido> pedidosExpirados = pedidoRepository.findByStatusAndDataCriacaoBefore(StatusPedido.PENDENTE, limite);

        if (!pedidosExpirados.isEmpty()) {
            logger.info("Encontrados {} pedidos expirados. Iniciando cancelamento automático...", pedidosExpirados.size());
            for (Pedido pedido : pedidosExpirados) {
                try {
                    pedidoService.cancelarPedidoExpirado(pedido);
                    logger.info("Pedido #{} cancelado automaticamente por expiração de tempo.", pedido.getId());
                } catch (Exception e) {
                    logger.error("Erro ao cancelar o pedido #{} automaticamente: {}", pedido.getId(), e.getMessage(), e);
                }
            }
        } else {
            logger.info("Nenhum pedido pendente expirado encontrado.");
        }
    }
}
