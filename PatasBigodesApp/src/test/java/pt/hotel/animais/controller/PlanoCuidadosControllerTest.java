package pt.hotel.animais.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PlanoCuidadosControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @Disabled("Requires seeded data and Docker environment; generated as a skeleton")
    public void viewPlano_shouldReturnOk_forActiveEstadia() throws Exception {
        mvc.perform(get("/plano-cuidados").param("estadiaId", "1"))
            .andExpect(status().isOk());
    }
}
