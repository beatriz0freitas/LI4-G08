package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void rootDeveRetornarPaginaHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/index"))
                .andExpect(model().attribute("activePage", "home"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void homeDaRececaoDeveMostrarDisponibilidadeSemAcessoALimpeza() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/reservas/disponibilidade")))
                .andExpect(content().string(not(containsString("/limpeza"))));
    }

    @Test
    @WithMockUser(roles = "RESPONSAVEL_LIMPEZA")
    void homeDaLimpezaDeveMostrarApenasOModuloOperacionalPermitido() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/limpeza")))
                .andExpect(content().string(not(containsString("/reservas/disponibilidade"))));
    }

    @Test
    @WithMockUser
    void loginDeveRetornarPaginaLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("form-signin")));
    }
}
