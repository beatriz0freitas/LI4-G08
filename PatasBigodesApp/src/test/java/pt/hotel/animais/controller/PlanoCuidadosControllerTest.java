package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.config.SecurityConfig;
import pt.hotel.animais.dto.PlanoCuidadosDto;
import pt.hotel.animais.service.IPlanoCuidadosService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = PlanoCuidadosController.class)
@Import(SecurityConfig.class)
class PlanoCuidadosControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IPlanoCuidadosService planoCuidadosService;

    @Test
    @WithMockUser(roles = {"CUIDADOR"})
    void viewPlanoDeveRenderizarPlanoDaEstadia() throws Exception {
        when(planoCuidadosService.getPlanoByEstadia(1L)).thenReturn(new PlanoCuidadosDto());

        mvc.perform(get("/plano-cuidados").param("estadiaId", "1"))
            .andExpect(status().isOk())
            .andExpect(view().name("cuidados/plano"))
            .andExpect(model().attribute("estadiaId", 1L));
    }
}
