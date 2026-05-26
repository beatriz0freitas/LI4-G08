package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.config.SecurityConfig;
import pt.hotel.animais.dto.auditoria.AuditoriaFiltroDTO;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.auditoria.AuditoriaEvento;
import pt.hotel.animais.model.enums.ResultadoAuditoria;
import pt.hotel.animais.model.enums.TipoColaborador;
import pt.hotel.animais.service.IColaboradorService;
import pt.hotel.animais.service.auditoria.IAuditoriaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AuditoriaController.class)
@Import(SecurityConfig.class)
class AuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAuditoriaService auditoriaService;

    @MockBean
    private IColaboradorService colaboradorService;

    @Test
    @WithMockUser(roles = "DIRETOR")
    void listarAuditoriaDiretorDeveRenderizarPagina() throws Exception {
        when(auditoriaService.consultarPorPeriodo(any(), any(), any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(eventoAuditoria())));
        when(colaboradorService.listarTodos()).thenReturn(List.of(colaborador()));

        mockMvc.perform(get("/auditoria")
                .param("dataInicio", "2026-05-01")
                .param("dataFim", "2026-05-31"))
            .andExpect(status().isOk())
            .andExpect(view().name("auditoria/list"))
            .andExpect(model().attributeExists("eventos"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("CRIAR_COLABORADOR")));
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void listarAuditoriaOutrosPerfisDeveNegarAcesso() throws Exception {
        mockMvc.perform(get("/auditoria"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void filtrosPorDataDevemSerEnviadosAoServico() throws Exception {
        when(auditoriaService.consultarPorPeriodo(any(), any(), any(), any(Pageable.class)))
            .thenReturn(Page.empty());
        when(colaboradorService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/auditoria")
                .param("dataInicio", "2026-05-01")
                .param("dataFim", "2026-05-31")
                .param("operacao", "CRIAR"))
            .andExpect(status().isOk());

        ArgumentCaptor<AuditoriaFiltroDTO> filtrosCaptor = ArgumentCaptor.forClass(AuditoriaFiltroDTO.class);
        verify(auditoriaService).consultarPorPeriodo(
            org.mockito.ArgumentMatchers.eq(LocalDate.of(2026, 5, 1)),
            org.mockito.ArgumentMatchers.eq(LocalDate.of(2026, 5, 31)),
            filtrosCaptor.capture(),
            any(Pageable.class)
        );
        assertThat(filtrosCaptor.getValue().getOperacao()).isEqualTo("CRIAR");
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void exportarCsvDeveDevolverFicheiro() throws Exception {
        when(auditoriaService.consultarPorPeriodo(any(), any(), any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(eventoAuditoria())));

        mockMvc.perform(get("/auditoria/exportar/csv")
                .param("dataInicio", "2026-05-01")
                .param("dataFim", "2026-05-31"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"auditoria.csv\""))
            .andExpect(content().contentTypeCompatibleWith("text/csv"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("id,timestamp,utilizador,operacao")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("CRIAR_COLABORADOR")));
    }

    private AuditoriaEvento eventoAuditoria() {
        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setId(1L);
        evento.setTimestamp(LocalDateTime.of(2026, 5, 26, 10, 0));
        evento.setUtilizador(colaborador());
        evento.setOperacao("CRIAR_COLABORADOR");
        evento.setEntidade("Colaborador");
        evento.setEntityId(10L);
        evento.setAcao("CREATE");
        evento.setDetalhes(Map.of("username", "novo"));
        evento.setResultado(ResultadoAuditoria.SUCESSO);
        return evento;
    }

    private Colaborador colaborador() {
        Colaborador colaborador = new Colaborador();
        colaborador.setId(1L);
        colaborador.setNome("Diretor");
        colaborador.setUsername("diretor");
        colaborador.setEmail("diretor@hotel.local");
        colaborador.setPasswordHash("hash");
        colaborador.setTipoColaborador(TipoColaborador.DIRETOR);
        colaborador.setAtivo(true);
        return colaborador;
    }
}
