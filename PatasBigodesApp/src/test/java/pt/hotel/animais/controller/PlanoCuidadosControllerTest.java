package pt.hotel.animais.controller;

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
import pt.hotel.animais.model.enums.PrioridadePlano;
import pt.hotel.animais.service.IPlanoCuidadosService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = PlanoCuidadosController.class)
@Import(SecurityConfig.class)
class PlanoCuidadosControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IPlanoCuidadosService planoCuidadosService;

    /**
     * LAC-02: Teste para visualizar plano ativo de uma estadia.
     */
    @Test
    @WithMockUser(roles = {"CUIDADOR"})
    void viewPlanoDeveRenderizarPlanoDaEstadia() throws Exception {
        PlanoCuidadosDto dto = new PlanoCuidadosDto();
        dto.setId(1L);
        dto.setEstadiaId(1L);
        dto.setAnimalId(1L);
        dto.setDataInicio(LocalDateTime.now());
        dto.setDataFim(LocalDateTime.now().plusDays(5));
        dto.setPrioridade(PrioridadePlano.ROTINA);
        dto.setAtivo(true);
        dto.setInstrucoes("Instrucoes iniciais");
        dto.setTarefas(new ArrayList<>());

        when(planoCuidadosService.obterPlanoPorEstadia(1L)).thenReturn(dto);

        mvc.perform(get("/plano-cuidados").param("estadiaId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("cuidados/plano"))
                .andExpect(model().attributeExists("plano"))
                .andExpect(model().attribute("estadiaId", 1L))
                .andExpect(model().attributeExists("prioridades"));
    }

    /**
     * LAC-02: Teste para criar novo plano de cuidados.
     */
    @Test
    @WithMockUser(roles = {"MEDICO_VETERINARIO"})
    void criarPlanoDeveRedirecionarParaPlanoCriado() throws Exception {
        PlanoCuidadosDto dto = new PlanoCuidadosDto();
        dto.setId(1L);
        when(planoCuidadosService.criarPlanoParaEstadia(1L, 1L)).thenReturn(dto);

        mvc.perform(post("/plano-cuidados/criar")
                        .with(csrf())
                        .param("estadiaId", "1")
                        .param("animalId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));
    }

    /**
     * LAC-02: Teste para adicionar tarefa ao plano.
     */
    @Test
    @WithMockUser(roles = {"CUIDADOR"})
    void adicionarTarefaDeveRedirecionarComSucesso() throws Exception {
        TarefaCuidadoDto tarefaDto = new TarefaCuidadoDto();
        tarefaDto.setId(1L);
        when(planoCuidadosService.adicionarTarefa(anyLong(), any(), anyLong()))
                .thenReturn(tarefaDto);

        mvc.perform(post("/plano-cuidados/1/tarefa")
                        .with(csrf())
                        .param("tipo", "ALIMENTACAO")
                        .param("descricao", "Alimentar animal")
                        .param("periodicidade", "DIARIA")
                        .param("dataHora", "2026-05-26T10:00")
                        .param("estadiaId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));
    }

    /**
     * LAC-02: Teste para marcar tarefa como concluida.
     */
    @Test
    @WithMockUser(roles = {"CUIDADOR"})
    void marcarTarefaConcluida() throws Exception {
        doNothing().when(planoCuidadosService).marcarTarefaConcluida(anyLong(), anyLong());

        mvc.perform(post("/plano-cuidados/tarefa/1/concluir")
                        .with(csrf())
                        .param("estadiaId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));
    }

    /**
     * LAC-02: Teste para adicionar instrucoes ao plano.
     */
    @Test
    @WithMockUser(roles = {"MEDICO_VETERINARIO"})
    void adicionarInstrucoesAoPlano() throws Exception {
        doNothing().when(planoCuidadosService).adicionarInstrucoes(anyLong(), any(), anyLong());

        mvc.perform(post("/plano-cuidados/1/instrucoes")
                        .with(csrf())
                        .param("instrucoes", "Nova instrucao")
                        .param("estadiaId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));
    }

    /**
     * LAC-02: Teste para atualizar prioridade do plano.
     */
    @Test
    @WithMockUser(roles = {"MEDICO_VETERINARIO"})
    void atualizarPrioridadeParaCritico() throws Exception {
        doNothing().when(planoCuidadosService)
                .atualizarPrioridade(anyLong(), eq(PrioridadePlano.CRITICO), anyLong());

        mvc.perform(post("/plano-cuidados/1/prioridade")
                        .with(csrf())
                        .param("prioridade", "CRITICO")
                        .param("estadiaId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));
    }

    /**
     * LAC-02: Teste para listar historico de planos do animal.
     */
    @Test
    @WithMockUser(roles = {"CUIDADOR"})
    void historicoPlanosDoAnimal() throws Exception {
        PlanoCuidadosDto plano = new PlanoCuidadosDto();
        plano.setId(1L);
        plano.setAnimalId(1L);

        var planosPage = new PageImpl<>(List.of(plano), PageRequest.of(0, 10), 1);
        when(planoCuidadosService.listarPlanosDoAnimal(1L, PageRequest.of(0, 10)))
                .thenReturn(planosPage);

        mvc.perform(get("/plano-cuidados/animal/1/historico"))
                .andExpect(status().isOk())
                .andExpect(view().name("cuidados/historico"))
                .andExpect(model().attributeExists("planosPage"))
                .andExpect(model().attribute("animalId", 1L));
    }

    /**
     * LAC-02: Teste para encerrar plano, chamado no check-out.
     */
    @Test
    @WithMockUser(roles = {"MEDICO_VETERINARIO"})
    void encerrarPlano() throws Exception {
        doNothing().when(planoCuidadosService).encerrarPlano(1L);

        mvc.perform(post("/plano-cuidados/1/encerrar")
                        .with(csrf())
                        .param("estadiaId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estadias?estadiaId=1"));
    }
}
