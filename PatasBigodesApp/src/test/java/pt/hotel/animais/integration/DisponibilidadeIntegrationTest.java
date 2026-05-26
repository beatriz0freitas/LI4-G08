package pt.hotel.animais.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.config.SecurityConfig;
import pt.hotel.animais.controller.ReservaController;
import pt.hotel.animais.dto.DisponibilidadeAlojamentoDto;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.service.IAlojamentoService;
import pt.hotel.animais.service.IAnimalService;
import pt.hotel.animais.service.IPagamentoService;
import pt.hotel.animais.service.IReservaService;
import pt.hotel.animais.service.ITutorService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = ReservaController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class DisponibilidadeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IReservaService reservaService;

    @MockBean
    private IAlojamentoService alojamentoService;

    @MockBean
    private ITutorService tutorService;

    @MockBean
    private IAnimalService animalService;

    @MockBean
    private EstadiaRepository estadiaRepository;

    @MockBean
    private IPagamentoService pagamentoService;

    @Test
    @WithMockUser(username = "10", roles = {"FUNCIONARIO_RECEPCAO"})
    void getFormularioDeDisponibilidadeDeveRenderizarTemplate() throws Exception {
        assertMocksInjected();

        mockMvc.perform(get("/reservas/disponibilidade"))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/disponibilidade"))
            .andExpect(model().attribute("pageTitle", "Consultar Disponibilidade"));
    }

    @Test
    @WithMockUser(username = "10", roles = {"FUNCIONARIO_RECEPCAO"})
    void postProcurarDisponibilidadeDeveRenderizarResultados() throws Exception {
        assertMocksInjected();

        LocalDate dataInicio = LocalDate.now().plusDays(14);
        LocalDate dataFim = dataInicio.plusDays(2);
        DisponibilidadeAlojamentoDto dto = new DisponibilidadeAlojamentoDto(5L, "Box A1", "CANINO", 2);
        dto.setDataInicio(dataInicio);
        dto.setDataFim(dataFim);
        dto.setDisponivel(true);

        when(alojamentoService.consultarDisponibilidade(eq(dataInicio), eq(dataFim))).thenReturn(List.of(dto));

        mockMvc.perform(post("/reservas/procurar-disponibilidade")
                .with(csrf())
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/index"))
            .andExpect(model().attribute("dataInicio", dataInicio))
            .andExpect(model().attribute("dataFim", dataFim));

        verify(alojamentoService).consultarDisponibilidade(dataInicio, dataFim);
    }

    private void assertMocksInjected() {
        if (reservaService == null || alojamentoService == null || tutorService == null || animalService == null || estadiaRepository == null || pagamentoService == null) {
            throw new IllegalStateException("Mock beans not injected");
        }
    }
}
