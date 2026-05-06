package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LimpezaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlojamentoRepository alojamentoRepository;

    @Test
    @WithMockUser(username = "limpeza", roles = {"RESPONSAVEL_LIMPEZA"})
    void listarPendentesDeveMostrarAlojamentosPendentes() throws Exception {
        mockMvc.perform(get("/limpeza"))
                .andExpect(status().isOk())
                .andExpect(view().name("limpeza/listar"))
                .andExpect(model().attributeExists("alojamentosPendentes"));
    }

    @Test
    @WithMockUser(username = "limpeza", roles = {"RESPONSAVEL_LIMPEZA"})
    void marcarComoLimpoDeveAtualizarOEstado() throws Exception {
        var alojamento = alojamentoRepository.findByEstadoLimpeza(EstadoLimpeza.PENDENTE).get(0);

        mockMvc.perform(post("/limpeza/" + alojamento.getId() + "/limpo")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/limpeza"));

        var atualizado = alojamentoRepository.findById(alojamento.getId()).orElseThrow();
        assertThat(atualizado.getEstadoLimpeza()).isEqualTo(EstadoLimpeza.CONCLUIDO);
    }
}
