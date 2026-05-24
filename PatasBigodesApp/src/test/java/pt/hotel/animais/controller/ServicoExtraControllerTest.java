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
public class ServicoExtraControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @Disabled("Skeleton test; requires data seeding and Docker")
    public void createExtra_shouldReturnRedirect() throws Exception {
        mvc.perform(post("/extras/create").param("estadiaId", "1").param("tipo", "BANHO").param("custo", "15.0"))
            .andExpect(status().is3xxRedirection());
    }
}
