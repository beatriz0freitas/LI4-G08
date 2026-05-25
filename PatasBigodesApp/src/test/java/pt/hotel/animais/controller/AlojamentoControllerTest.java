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
import pt.hotel.animais.service.IAlojamentoService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void listarDeveRenderizarAlojamentos() throws Exception {
        Alojamento a = new Alojamento(1L, "Box 1", null, null, EstadoLimpeza.CONCLUIDO, null);
        when(alojamentoService.listarTodos()).thenReturn(List.of(a));

        mockMvc.perform(get("/alojamentos"))
                .andExpect(status().isOk())
                .andExpect(view().name("alojamento/listar"))
                .andExpect(model().attribute("activePage", "alojamentos"))
                .andExpect(model().attributeExists("alojamentos"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void listarComoFuncionarioDeveRetornarPagina() throws Exception {
        when(alojamentoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/alojamentos"))
                .andExpect(status().isOk())
                .andExpect(view().name("alojamento/listar"));
    }
}
