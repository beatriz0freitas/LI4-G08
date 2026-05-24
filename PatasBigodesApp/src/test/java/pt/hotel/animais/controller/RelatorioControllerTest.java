package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.dto.RelatorioResumoDto;
import pt.hotel.animais.service.IRelatorioService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(RelatorioController.class)
@AutoConfigureMockMvc(addFilters = false)
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRelatorioService relatorioService;

    @Test
    @WithMockUser(roles = "DIRETOR")
    void indexDeveRenderizarRelatorio() throws Exception {
        when(relatorioService.gerarRelatorio(any())).thenReturn(new RelatorioResumoDto());

        mockMvc.perform(get("/relatorios"))
            .andExpect(status().isOk())
            .andExpect(view().name("relatorios/list"))
            .andExpect(model().attributeExists("relatorio"))
            .andExpect(model().attributeExists("tiposAlojamento"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void gerarComPeriodoInvalidoDeveMostrarErro() throws Exception {
        mockMvc.perform(post("/relatorios/gerar")
                .with(csrf())
                .param("dataInicio", "2026-05-31")
                .param("dataFim", "2026-05-01")
                .param("agruparPor", "MES"))
            .andExpect(status().isOk())
            .andExpect(view().name("relatorios/list"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void exportarCsvDeveDevolverFicheiro() throws Exception {
        when(relatorioService.gerarCsv(any())).thenReturn("periodo_start,periodo_end\n");

        mockMvc.perform(get("/relatorios/exportar/csv")
                .param("dataInicio", "2026-05-01")
                .param("dataFim", "2026-05-31"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"relatorio.csv\""))
            .andExpect(content().contentTypeCompatibleWith("text/csv"));
    }
}
