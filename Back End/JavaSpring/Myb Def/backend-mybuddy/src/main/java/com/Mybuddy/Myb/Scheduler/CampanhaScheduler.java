package com.Mybuddy.Myb.Scheduler;

import com.Mybuddy.Myb.Service.CampanhaDoacaoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampanhaScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CampanhaScheduler.class);

    private final CampanhaDoacaoService campanhaService;

    /**
     * Executa diariamente à meia-noite (cron = "0 0 0 * * ?")
     * buscando campanhas ATIVAS com dataExpiracao anterior ao momento atual
     * e alterando o status para ENCERRADA.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void expirarCampanhasVencidas() {
        logger.info("Iniciando rotina diária de expiração de campanhas...");
        try {
            int expiradas = campanhaService.expirarCampanhasAtivasExpiradas();
            logger.info("Rotina de expiração concluída. {} campanhas foram encerradas.", expiradas);
        } catch (Exception e) {
            logger.error("Erro ao executar rotina diária de expiração de campanhas: {}", e.getMessage(), e);
        }
    }
}
