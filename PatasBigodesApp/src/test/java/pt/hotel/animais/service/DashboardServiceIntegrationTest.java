package pt.hotel.animais.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class DashboardServiceIntegrationTest {

    @Autowired
    private DashboardService dashboardService;

    @Test
    void dashboardService_deve_agregar_metricas_via_services() {
        Assertions.assertNotNull(dashboardService);
        Assertions.assertTrue(dashboardService.contarAlojamentosDisponiveis() >= 0);
        Assertions.assertTrue(dashboardService.contarAlojamentosPendentesLimpeza() >= 0);
        Assertions.assertTrue(dashboardService.contarReservasAtivas() >= 0);
        Assertions.assertTrue(dashboardService.contarReservasFuturas() >= 0);
        Assertions.assertTrue(dashboardService.contarEstadiasAtivas() >= 0);
        Assertions.assertTrue(dashboardService.contarPagamentosPendentes() >= 0);
        Assertions.assertNotNull(dashboardService.faturacaoTotal());
    }
}
