package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.config.SecurityConfig;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.repository.NotaRepository;
import pt.hotel.animais.repository.RegistoCuidadoRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;
import pt.hotel.animais.service.IHistoricoService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistoricoController.class)
@Import(SecurityConfig.class)
class HistoricoAuthorizationMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IHistoricoService historicoService;

    @MockBean
    private RegistoCuidadoRepository registoCuidadoRepository;

    @MockBean
    private ServicoExtraRepository servicoExtraRepository;

    @MockBean
    private IntervencaoClinicaRepository intervencaoClinicaRepository;

    @MockBean
    private NotaRepository notaRepository;

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void rececaoPodeConsultarHistorico() throws Exception {
        when(historicoService.listarHistorico(any(), any(), any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/historico"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void cuidadorNaoPodeConsultarHistorico() throws Exception {
        mockMvc.perform(get("/historico"))
            .andExpect(status().isForbidden());
    }
}
