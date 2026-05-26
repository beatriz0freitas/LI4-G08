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
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.service.IAlojamentoService;
import pt.hotel.animais.service.IAnimalService;
import pt.hotel.animais.service.IPagamentoService;
import pt.hotel.animais.service.IReservaService;
import pt.hotel.animais.service.ITutorService;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservaController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class ReservaConfirmIntegrationTest {

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
    @WithMockUser(username = "12", roles = {"FUNCIONARIO_RECEPCAO"})
    void confirmarReservaNaoDeveChamarServicoPorqueConfirmacaoOcorreNoCheckIn() throws Exception {
        assertMocksInjected();

        mockMvc.perform(post("/reservas/42/confirmar").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/reservas/42"));

        verify(reservaService, never()).confirmar(42L);
        verify(reservaService, never()).concluir(42L);
    }

    @Test
    @WithMockUser(username = "12", roles = {"FUNCIONARIO_RECEPCAO"})
    void concluirReservaNaoDeveChamarServicoDeConclusaoManual() throws Exception {
        assertMocksInjected();

        mockMvc.perform(post("/reservas/42/concluir").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/reservas/42"));

        verifyNoInteractions(reservaService);
    }

    private void assertMocksInjected() {
        if (reservaService == null || alojamentoService == null || tutorService == null || animalService == null || estadiaRepository == null || pagamentoService == null) {
            throw new IllegalStateException("Mock beans not injected");
        }
    }
}
