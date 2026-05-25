package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.service.IDashboardService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDashboardService dashboardService;

    @Test
    @WithMockUser(username = "diretor", roles = {"DIRETOR"})
    void dashboardDeveExibirIndicadoresEAtributos() throws Exception {
        when(dashboardService.contarAlojamentosDisponiveis()).thenReturn(8L);
        when(dashboardService.contarAlojamentosTotal()).thenReturn(12L);
        when(dashboardService.contarAlojamentosOcupados()).thenReturn(4L);
        when(dashboardService.contarAlojamentosPendentesLimpeza()).thenReturn(1L);
        when(dashboardService.contarReservasAtivas()).thenReturn(5L);
        when(dashboardService.contarReservasFuturas()).thenReturn(3L);
        when(dashboardService.contarEstadiasAtivas()).thenReturn(4L);
        when(dashboardService.contarPagamentosPendentes()).thenReturn(2L);
        when(dashboardService.faturacaoTotal()).thenReturn(new BigDecimal("250.00"));
        when(dashboardService.taxaOcupacao()).thenReturn(33.33);
        when(dashboardService.faturacaoMesAtual()).thenReturn(new BigDecimal("120.00"));
        when(dashboardService.listarEstadiasEmCurso()).thenReturn(List.of());

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attributeExists("alojamentosDisponiveis"))
                .andExpect(model().attributeExists("reservasAtivas"))
                .andExpect(model().attributeExists("estadiasEmCurso"))
                .andExpect(model().attributeExists("pagamentosPendentes"))
                .andExpect(model().attributeExists("taxaOcupacao"))
                .andExpect(model().attributeExists("faturacaoMes"))
                .andExpect(model().attributeExists("estadiasDashboard"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Taxa de ocupação")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Faturação (mês)")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Estadias em curso")));
    }
}
