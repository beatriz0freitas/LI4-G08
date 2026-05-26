package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.config.SecurityConfig;
import pt.hotel.animais.dto.RelatorioResumoDto;
import pt.hotel.animais.service.IColaboradorService;
import pt.hotel.animais.service.IAlojamentoService;
import pt.hotel.animais.service.IDashboardService;
import pt.hotel.animais.service.IRelatorioService;
import pt.hotel.animais.service.auditoria.IAuditoriaService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ColaboradorController.class, RelatorioController.class, DashboardController.class, AlojamentoController.class, AuditoriaController.class})
@Import(SecurityConfig.class)
class SecurityAuthorizationMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IColaboradorService colaboradorService;

    @MockBean
    private IRelatorioService relatorioService;

    @MockBean
    private IDashboardService dashboardService;

    @MockBean
    private IAlojamentoService alojamentoService;

    @MockBean
    private IAuditoriaService auditoriaService;

    @Test
    void rotaSensivelSemAutenticacaoRedirecionaParaLogin() throws Exception {
        mockMvc.perform(get("/colaboradores"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO_RECEPCAO")
    void rececaoNaoAcedeAColaboradores() throws Exception {
        mockMvc.perform(get("/colaboradores"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void cuidadorNaoAcedeARelatorios() throws Exception {
        mockMvc.perform(get("/relatorios"))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/auditoria"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RESPONSAVEL_LIMPEZA")
    void responsavelLimpezaNaoAcedeAListaGeralDeAlojamentos() throws Exception {
        mockMvc.perform(get("/alojamentos"))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/alojamentos/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void diretorAcedeAColaboradoresERelatorios() throws Exception {
        when(colaboradorService.listarTodos()).thenReturn(List.of());
        when(relatorioService.gerarRelatorio(any())).thenReturn(new RelatorioResumoDto());
        when(auditoriaService.consultarPorPeriodo(any(), any(), any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/colaboradores"))
            .andExpect(status().isOk());
        mockMvc.perform(get("/relatorios"))
            .andExpect(status().isOk());
        mockMvc.perform(get("/auditoria"))
            .andExpect(status().isOk());
    }
}
