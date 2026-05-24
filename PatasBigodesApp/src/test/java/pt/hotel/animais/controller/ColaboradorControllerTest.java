package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.enums.TipoColaborador;
import pt.hotel.animais.service.IColaboradorService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ColaboradorController.class)
@AutoConfigureMockMvc(addFilters = false)
class ColaboradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IColaboradorService colaboradorService;

    @Test
    @WithMockUser(roles = "DIRETOR")
    void listarDeveRenderizarColaboradores() throws Exception {
        when(colaboradorService.listarTodos()).thenReturn(List.of(new Colaborador()));

        mockMvc.perform(get("/colaboradores"))
            .andExpect(status().isOk())
            .andExpect(view().name("colaboradores/list"))
            .andExpect(model().attributeExists("colaboradores"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void novoDevePopularTiposColaborador() throws Exception {
        mockMvc.perform(get("/colaboradores/novo"))
            .andExpect(status().isOk())
            .andExpect(view().name("colaboradores/form"))
            .andExpect(model().attributeExists("tiposColaborador"))
            .andExpect(model().attributeExists("colaboradorForm"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void criarSemTipoDeveVoltarAoFormularioComErros() throws Exception {
        mockMvc.perform(post("/colaboradores")
                .with(csrf())
                .param("username", "novo")
                .param("nome", "Novo Colaborador")
                .param("email", "novo@hotel.local")
                .param("password", "segredo123")
                .param("ativo", "true"))
            .andExpect(status().isOk())
            .andExpect(view().name("colaboradores/form"))
            .andExpect(model().attributeHasFieldErrors("colaboradorForm", "tipoColaborador"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void criarValidoDeveRedirecionarParaLista() throws Exception {
        Colaborador colaborador = new Colaborador();
        colaborador.setNome("Novo Colaborador");
        when(colaboradorService.criar(org.mockito.ArgumentMatchers.any())).thenReturn(colaborador);

        mockMvc.perform(post("/colaboradores")
                .with(csrf())
                .param("username", "novo")
                .param("nome", "Novo Colaborador")
                .param("email", "novo@hotel.local")
                .param("password", "segredo123")
                .param("tipoColaborador", TipoColaborador.CUIDADOR.name())
                .param("ativo", "true"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/colaboradores"));
    }
}
