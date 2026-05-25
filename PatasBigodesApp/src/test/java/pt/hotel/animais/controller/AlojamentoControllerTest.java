package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.TipoAlojamento;
import pt.hotel.animais.service.IAlojamentoService;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AlojamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlojamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAlojamentoService alojamentoService;

    @Test
    @WithMockUser(roles = "DIRETOR")
    void listarMostraZeroReservasAtivasELinkDeDetalhe() throws Exception {
        Alojamento alojamento = new Alojamento(1L, "C-01", TipoAlojamento.CANINO, 1, EstadoLimpeza.CONCLUIDO, null);
        when(alojamentoService.listarTodos()).thenReturn(List.of(alojamento));
        when(alojamentoService.contarAlojamentosComReservasAtivas()).thenReturn(0L);

        mockMvc.perform(get("/alojamentos"))
            .andExpect(status().isOk())
            .andExpect(view().name("alojamento/listar"))
            .andExpect(model().attribute("alojamentosComReservasAtivas", 0L))
            .andExpect(content().string(containsString("Com Reservas Ativas")))
            .andExpect(content().string(containsString(">0</span>")))
            .andExpect(content().string(containsString("/alojamentos/1")));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void detalheMostraDadosDoAlojamento() throws Exception {
        Alojamento alojamento = new Alojamento(1L, "C-01", TipoAlojamento.CANINO, 1, EstadoLimpeza.CONCLUIDO, null);
        when(alojamentoService.obter(1L)).thenReturn(alojamento);

        mockMvc.perform(get("/alojamentos/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("alojamento/detalhe"))
            .andExpect(content().string(containsString("Detalhe do Alojamento")))
            .andExpect(content().string(containsString("C-01")))
            .andExpect(content().string(containsString("Canino")));
    }
}
