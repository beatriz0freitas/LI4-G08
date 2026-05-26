package pt.hotel.animais.job;

import org.junit.jupiter.api.Test;
import pt.hotel.animais.service.auditoria.IAuditoriaService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditoriaSchedulerJobTest {

    @Test
    void limparAuditoriaAntigaDeveAplicarRetencaoDeDozeMeses() {
        IAuditoriaService auditoriaService = mock(IAuditoriaService.class);
        when(auditoriaService.limparzardosAntigos(1)).thenReturn(2L);
        AuditoriaSchedulerJob job = new AuditoriaSchedulerJob(auditoriaService);

        job.limparAuditoriaAntiga();

        verify(auditoriaService).limparzardosAntigos(1);
    }
}
