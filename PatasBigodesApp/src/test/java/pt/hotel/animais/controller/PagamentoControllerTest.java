package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.service.IPagamentoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PagamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPagamentoService pagamentoService;

    @Test
    @WithMockUser(roles = "DIRETOR")
    void indexDeveRenderizarPaginaPagamentos() throws Exception {
        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/index"))
                .andExpect(model().attribute("activePage", "pagamentos"))
                .andExpect(model().attribute("pageTitle", "Pagamentos"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void registrarValidoDeveRedirecionarParaHistorico() throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .with(csrf())
                        .param("estadiaId", "1")
                        .param("metodo", "NUMERARIO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/historico"))
                .andExpect(flash().attribute("successMessage", "Pagamento registado com sucesso"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void registrarComErroDeveAdicionarMensagemFlash() throws Exception {
        doThrow(new IllegalArgumentException("Estadia não encontrada"))
                .when(pagamentoService).registrarPagamento(any());

        mockMvc.perform(post("/pagamentos")
                        .with(csrf())
                        .param("estadiaId", "99")
                        .param("metodo", "NUMERARIO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/historico"))
                .andExpect(flash().attribute("errorMessage", "Estadia não encontrada"));
    }
}
