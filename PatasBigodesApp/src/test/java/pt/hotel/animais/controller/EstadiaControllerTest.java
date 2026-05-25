package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.service.IEstadiaService;
import pt.hotel.animais.service.IPagamentoService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(EstadiaController.class)
class EstadiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEstadiaService estadiaService;

    @MockBean
    private EstadiaRepository estadiaRepository;

    @MockBean
    private IPagamentoService pagamentoService;

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void operacoesDeveRenderizarPagina() throws Exception {
        mockMvc.perform(get("/estadias"))
                .andExpect(status().isOk())
                .andExpect(view().name("estadias/checkin-checkout"))
                .andExpect(model().attribute("activePage", "estadias"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void operacoesComEstadiaIdDeveAdicionarExtrasAoModelo() throws Exception {
        Estadia e = new Estadia();
        e.setId(1L);
        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(e));
        when(pagamentoService.calcularExtras(any())).thenReturn(new BigDecimal("25.00"));

        mockMvc.perform(get("/estadias").param("estadiaId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("estadias/checkin-checkout"))
                .andExpect(model().attributeExists("extrasTotal"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void checkInValidoDeveRedirecionarParaEstadiasComSucesso() throws Exception {
        Estadia e = new Estadia();
        e.setId(5L);
        when(estadiaService.abrirEstadiaPorReserva(10L)).thenReturn(e);

        mockMvc.perform(post("/estadias/check-in")
                        .with(csrf())
                        .param("reservaId", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estadias"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void checkInComErroDeveRedirecionarParaReservas() throws Exception {
        when(estadiaService.abrirEstadiaPorReserva(99L))
                .thenThrow(new IllegalArgumentException("Reserva não encontrada"));

        mockMvc.perform(post("/estadias/check-in")
                        .with(csrf())
                        .param("reservaId", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void checkOutValidoDeveRedirecionarParaHistorico() throws Exception {
        Estadia e = new Estadia();
        e.setId(3L);
        when(estadiaService.checkOut(3L)).thenReturn(e);

        mockMvc.perform(post("/estadias/check-out")
                        .with(csrf())
                        .param("estadiaId", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/historico"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void checkOutComErroDeveRedirecionarParaHistoricoComErro() throws Exception {
        when(estadiaService.checkOut(99L))
                .thenThrow(new IllegalArgumentException("Estadia já terminada"));

        mockMvc.perform(post("/estadias/check-out")
                        .with(csrf())
                        .param("estadiaId", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/historico"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
