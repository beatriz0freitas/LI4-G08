package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.service.IEstadiaService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void operacoesDeveRenderizarPagina() throws Exception {
        when(estadiaService.listarComFiltros(any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/estadias/lista"))
                .andExpect(status().isOk())
                .andExpect(view().name("estadias/lista"))
                .andExpect(model().attribute("activePage", "estadias"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void operacoesComEstadiaIdDeveAdicionarExtrasAoModelo() throws Exception {
        mockMvc.perform(get("/estadias"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estadias/lista"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void operacoesComReservaIdDeveAdicionarReservaEValorAoModelo() throws Exception {
        when(estadiaService.listarComFiltros(any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/estadias/lista"))
                .andExpect(status().isOk())
                .andExpect(view().name("estadias/lista"))
                .andExpect(model().attributeExists("estadias"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void checkInValidoDeveRedirecionarParaEstadiasComSucesso() throws Exception {
        Estadia e = new Estadia();
        e.setId(5L);
        when(estadiaService.abrirEstadiaPorReserva(10L, pt.hotel.animais.model.enums.MetodoPagamento.NUMERARIO)).thenReturn(e);

        mockMvc.perform(post("/estadias/check-in")
                        .with(csrf())
                        .param("reservaId", "10")
                        .param("metodoPagamento", "NUMERARIO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estadias"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void checkInComErroDeveRedirecionarParaReservas() throws Exception {
        when(estadiaService.abrirEstadiaPorReserva(99L, pt.hotel.animais.model.enums.MetodoPagamento.NUMERARIO))
                .thenThrow(new IllegalArgumentException("Reserva não encontrada"));

        mockMvc.perform(post("/estadias/check-in")
                        .with(csrf())
                        .param("reservaId", "99")
                        .param("metodoPagamento", "NUMERARIO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void checkInSemMetodoPagamentoDeveRejeitarSemValorPorDefeito() throws Exception {
        mockMvc.perform(post("/estadias/check-in")
                        .with(csrf())
                        .param("reservaId", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(estadiaService, never()).abrirEstadiaPorReserva(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void checkOutValidoDeveRedirecionarParaHistorico() throws Exception {
        Estadia e = new Estadia();
        e.setId(3L);
        when(estadiaService.checkOut(3L, pt.hotel.animais.model.enums.MetodoPagamento.NUMERARIO)).thenReturn(e);

        mockMvc.perform(post("/estadias/check-out")
                        .with(csrf())
                        .param("estadiaId", "3")
                        .param("metodoPagamento", "NUMERARIO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/historico"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void checkOutComErroDeveRedirecionarParaHistoricoComErro() throws Exception {
        when(estadiaService.checkOut(99L, pt.hotel.animais.model.enums.MetodoPagamento.NUMERARIO))
                .thenThrow(new IllegalArgumentException("Estadia já terminada"));

        mockMvc.perform(post("/estadias/check-out")
                        .with(csrf())
                        .param("estadiaId", "99")
                        .param("metodoPagamento", "NUMERARIO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/historico"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void checkOutSemMetodoPagamentoDeveRejeitarSemValorPorDefeito() throws Exception {
        mockMvc.perform(post("/estadias/check-out")
                        .with(csrf())
                        .param("estadiaId", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/historico"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(estadiaService, never()).checkOut(anyLong(), any());
    }

}
