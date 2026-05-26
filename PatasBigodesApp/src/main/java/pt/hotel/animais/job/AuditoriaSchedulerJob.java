package pt.hotel.animais.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.hotel.animais.service.auditoria.IAuditoriaService;

/**
 * Job de retenção para remover eventos de auditoria fora do prazo operacional.
 */
@Component
public class AuditoriaSchedulerJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaSchedulerJob.class);
    private static final int ANOS_RETENCAO = 1;

    private final IAuditoriaService auditoriaService;

    public AuditoriaSchedulerJob(IAuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    /**
     * Executa diariamente às 03h00 para aplicar a retenção de 12 meses.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void limparAuditoriaAntiga() {
        LOGGER.info("Limpeza de auditoria iniciada");
        long removidos = auditoriaService.limparzardosAntigos(ANOS_RETENCAO);
        LOGGER.info("Limpeza de auditoria concluída: {} evento(s) removido(s)", removidos);
    }
}
