package pt.hotel.animais.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.repository.PagamentoRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PagamentoCheckInIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Test
    @WithMockUser(roles = "USER")
    void pagamentoRepository_is_available() {
        long count = pagamentoRepository.count();
        Assertions.assertTrue(count >= 0);
    }

    @Test
    @WithMockUser(roles = "USER")
    void dashboard_endpoint_is_accessible() throws Exception {
        mockMvc.perform(get("/dashboard"))
            .andExpect(status().isOk());
    }
}
