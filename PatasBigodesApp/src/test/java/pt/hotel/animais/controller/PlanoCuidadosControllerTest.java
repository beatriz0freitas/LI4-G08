package pt.hotel.animais.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.config.SecurityConfig;
import pt.hotel.animais.dto.PlanoCuidadosDto;
import pt.hotel.animais.dto.TarefaCuidadoDto;
import pt.hotel.animais.model.enums.PeriodicidadeTarefa;
import pt.hotel.animais.model.enums.PrioridadePlano;
import pt.hotel.animais.service.IPlanoCuidadosService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = PlanoCuidadosController.class)
@Import(SecurityConfig.class)
class PlanoCuidadosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPlanoCuidadosService planoCuidadosService;

    private PlanoCuidadosDto planoDto;
    private TarefaCuidadoDto tarefaDto;

    @BeforeEach
    void setup() {
        tarefaDto = new TarefaCuidadoDto();
        tarefaDto.setId(1L);
        tarefaDto.setPlanoCuidadosId(1L);
        tarefaDto.setTipo("ALIMENTACAO");
        tarefaDto.setDescricao("Dar ração");
        tarefaDto.setPeriodicidade(PeriodicidadeTarefa.DIARIA);
        tarefaDto.setDataHora(LocalDateTime.of(2026, 5, 25, 10, 0));
        tarefaDto.setConcluida(false);

        planoDto = new PlanoCuidadosDto();
        planoDto.setId(1L);
        planoDto.setAnimalId(1L);
        planoDto.setEstadiaId(1L);
        planoDto.setDataInicio(LocalDateTime.of(2026, 5, 25, 9, 0));
        planoDto.setDataFim(LocalDateTime.of(2026, 5, 30, 18, 0));
        planoDto.setPrioridade(PrioridadePlano.ROTINA);
        planoDto.setAtivo(true);
        planoDto.setInstrucoes("Sem instruções especiais");
        planoDto.setTarefas(List.of(tarefaDto));
        planoDto.setAnimalNome("Luna");
        planoDto.setAlojamentoIdentificacao("Box 01");
        planoDto.setEstadoSaude("Normal");
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void entradaDePlanoCuidadosDeveApresentarQuadroAgregadoDoTurno() throws Exception {
        when(planoCuidadosService.listarPlanosAtivosDoTurno()).thenReturn(List.of(planoDto));

        mockMvc.perform(get("/plano-cuidados"))
            .andExpect(status().isOk())
            .andExpect(view().name("cuidados/lista-planos"))
            .andExpect(model().attributeExists("planosTurno"))
            .andExpect(model().attribute("totalAnimais", 1))
            .andExpect(model().attribute("tarefasPendentes", 1L))
            .andExpect(model().attribute("activePage", "plano-cuidados"))
            .andExpect(content().string(containsString("Progresso do turno")))
            .andExpect(content().string(containsString("Luna")));
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void viewPlanoDeveRenderizarPlanoDaEstadia() throws Exception {
        when(planoCuidadosService.obterPlanoPorEstadia(1L)).thenReturn(planoDto);

        mockMvc.perform(get("/plano-cuidados").param("estadiaId", "1"))
            .andExpect(status().isOk())
            .andExpect(view().name("cuidados/plano"))
            .andExpect(model().attribute("plano", planoDto))
            .andExpect(model().attribute("estadiaId", 1L))
            .andExpect(model().attribute("activePage", "plano-cuidados"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void criarPlanoDeveRedirecionarParaPlanoDaEstadia() throws Exception {
        when(planoCuidadosService.obterOuCriarPlanoParaEstadiaAtiva(1L)).thenReturn(planoDto);

        mockMvc.perform(post("/plano-cuidados/criar")
                .with(csrf())
                .param("estadiaId", "1")
                .param("animalId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));

        verify(planoCuidadosService).obterOuCriarPlanoParaEstadiaAtiva(1L);
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void adicionarTarefaDeveRedirecionarParaPlanoDaEstadia() throws Exception {
        when(planoCuidadosService.adicionarTarefa(eq(1L), any(), eq(1L))).thenReturn(tarefaDto);

        mockMvc.perform(post("/plano-cuidados/1/tarefa")
                .with(csrf())
                .param("tipo", "ALIMENTACAO")
                .param("descricao", "Dar ração")
                .param("periodicidade", "DIARIA")
                .param("dataHora", "2026-05-25T10:00")
                .param("estadiaId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));

        verify(planoCuidadosService).adicionarTarefa(eq(1L), any(), eq(1L));
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void marcarTarefaConcluidaDeveRedirecionarParaPlanoDaEstadia() throws Exception {
        mockMvc.perform(post("/plano-cuidados/tarefa/1/concluir")
                .with(csrf())
                .param("estadiaId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));

        verify(planoCuidadosService).marcarTarefaConcluida(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void adicionarInstrucoesDeveRedirecionarParaPlanoDaEstadia() throws Exception {
        mockMvc.perform(post("/plano-cuidados/1/instrucoes")
                .with(csrf())
                .param("instrucoes", "Evitar exercício intenso")
                .param("estadiaId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));

        verify(planoCuidadosService).adicionarInstrucoes(1L, "Evitar exercício intenso", 1L);
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void atualizarPrioridadeDeveRedirecionarParaPlanoDaEstadia() throws Exception {
        mockMvc.perform(post("/plano-cuidados/1/prioridade")
                .with(csrf())
                .param("prioridade", "URGENTE")
                .param("estadiaId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));

        verify(planoCuidadosService).atualizarPrioridade(1L, PrioridadePlano.URGENTE, 1L);
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void historicoPlanosDeveRenderizarHistoricoDoAnimal() throws Exception {
        when(planoCuidadosService.listarPlanosDoAnimal(eq(1L), any()))
            .thenReturn(new PageImpl<>(List.of(planoDto), PageRequest.of(0, 10), 11));

        mockMvc.perform(get("/plano-cuidados/animal/1/historico").param("page", "0"))
            .andExpect(status().isOk())
            .andExpect(view().name("cuidados/historico"))
            .andExpect(model().attributeExists("planosPage"))
            .andExpect(model().attribute("animalId", 1L))
            .andExpect(content().string(containsString("/plano-cuidados/animal/1/historico?page=1")))
            .andExpect(content().string(containsString("Próxima")));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void encerrarPlanoDeveRedirecionarParaEstadias() throws Exception {
        mockMvc.perform(post("/plano-cuidados/1/encerrar")
                .with(csrf())
                .param("estadiaId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/estadias?estadiaId=1"));

        verify(planoCuidadosService).encerrarPlano(1L);
    }
}
