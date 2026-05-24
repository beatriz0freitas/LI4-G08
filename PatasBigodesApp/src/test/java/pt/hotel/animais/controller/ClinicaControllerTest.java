package pt.hotel.animais.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ClinicaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @Disabled("Skeleton test; requires data seeding and Docker")
    public void createIntervencao_shouldReturnRedirect() throws Exception {
        mvc.perform(post("/clinica/intervencoes/create").param("estadiaId", "1").param("descricao", "Vacina"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @Disabled("Skeleton test; requires data seeding and Docker")
    public void createAlteracaoEstado_shouldReturnRedirect() throws Exception {
        mvc.perform(post("/clinica/alteracoes/create").param("estadiaId", "1").param("descricao", "Melhora").param("severidade", "BAIXA"))
            .andExpect(status().is3xxRedirection());
    }
}
