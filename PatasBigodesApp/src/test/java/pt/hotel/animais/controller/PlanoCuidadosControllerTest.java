package pt.hotel.animais.controller;






















































































































































































































}    }        assertTrue(historico.getContent().stream().anyMatch(p -> p.getId().equals(plan1.getId())));        assertTrue(historico.getTotalElements() >= 1);        // Assert        var historico = planoCuidadosService.listarPlanosDoAnimal(animal.getId(), PageRequest.of(0, 10));        // Act - Listar histórico do animal        var plan1 = planoCuidadosService.criarPlanoParaEstadia(estadia.getId(), animal.getId());        // Arrange    void duplVinculoAnimalHistoricoEstadiaUnica() throws Exception {    @Test     */     * LAC-02: Duplo vínculo - Animal mantém histórico, Estadia tem única instância ativa    /**    }        assertFalse(planoEncerrado.getAtivo());        var planoEncerrado = planoCuidadosService.obterPlanoPorEstadia(estadia.getId());        // Assert        planoCuidadosService.encerrarPlano(plano.getId());        // Act        PlanoCuidadosDto plano = planoCuidadosService.criarPlanoParaEstadia(estadia.getId(), animal.getId());        // Arrange    void encerrarPlanoDeveMarcarInativo() throws Exception {    @Test     */     * LAC-02: Encerrar plano    /**    }        assertEquals(PrioridadePlano.CRITICO, planoAtualizado.getPrioridade());        var planoAtualizado = planoCuidadosService.obterPlanoPorEstadia(estadia.getId());        // Assert        planoCuidadosService.atualizarPrioridade(plano.getId(), PrioridadePlano.CRITICO, 1L);        // Act        PlanoCuidadosDto plano = planoCuidadosService.criarPlanoParaEstadia(estadia.getId(), animal.getId());        // Arrange    void atualizarPrioridadeParaCritico() throws Exception {    @Test     */     * LAC-02: Atualizar prioridade (hook de escalação para CRITICO)    /**    }        assertTrue(planoAtualizado.getInstrucoes().contains(novaInstrucao));        var planoAtualizado = planoCuidadosService.obterPlanoPorEstadia(estadia.getId());        // Assert        planoCuidadosService.adicionarInstrucoes(plano.getId(), novaInstrucao, 1L);        // Act        String novaInstrucao = "Manter em local aquecido";        PlanoCuidadosDto plano = planoCuidadosService.criarPlanoParaEstadia(estadia.getId(), animal.getId());        // Arrange    void adicionarInstrucoesDeveAtualizarCampo() throws Exception {    @Test     */     * LAC-02: Adicionar instruções ao plano    /**    }        assertEquals(1L, tarefaAtualizada.getAutorConclusaoId());        assertTrue(tarefaAtualizada.getConcluida());        var tarefaAtualizada = planoAtualizado.getTarefas().get(0);        var planoAtualizado = planoCuidadosService.obterPlanoPorEstadia(estadia.getId());        // Assert - verificar que tarefa está marcada como concluída        planoCuidadosService.marcarTarefaConcluida(tarefa.getId(), 1L);        // Act        var tarefa = planoCuidadosService.adicionarTarefa(plano.getId(), form, 1L);        form.setDataHora(LocalDateTime.now());        form.setPeriodicidade(PeriodicidadeTarefa.UNICA);        form.setDescricao("Administrar medicação");        form.setTipo("MEDICACAO");        TarefaCuidadoFormDto form = new TarefaCuidadoFormDto();        PlanoCuidadosDto plano = planoCuidadosService.criarPlanoParaEstadia(estadia.getId(), animal.getId());        // Arrange    void marcarTarefaConcluídaDeveAtualizarStatus() throws Exception {    @Test     */     * LAC-02: Marcar tarefa como concluída    /**    }        assertEquals(PeriodicidadeTarefa.DIARIA, tarefa.getPeriodicidade());        assertEquals("ALIMENTACAO_MANHA", tarefa.getTipo());        assertNotNull(tarefa.getId());        // Assert        var tarefa = planoCuidadosService.adicionarTarefa(plano.getId(), form, 1L);        // Act        form.setDataHora(LocalDateTime.now().withHour(8).withMinute(0));        form.setPeriodicidade(PeriodicidadeTarefa.DIARIA);        form.setDescricao("Alimentar às 8h da manhã");        form.setTipo("ALIMENTACAO_MANHA");        TarefaCuidadoFormDto form = new TarefaCuidadoFormDto();        PlanoCuidadosDto plano = planoCuidadosService.criarPlanoParaEstadia(estadia.getId(), animal.getId());        // Arrange    void adicionarTarefaDeveIncluirNoPlano() throws Exception {    @Test     */     * LAC-02: Adicionar tarefa ao plano    /**    }        assertEquals(animal.getId(), plano.getAnimalId());        assertEquals(estadia.getId(), plano.getEstadiaId());        assertNotNull(plano);        // Assert        PlanoCuidadosDto plano = planoCuidadosService.obterPlanoPorEstadia(estadia.getId());        // Act        planoCuidadosService.criarPlanoParaEstadia(estadia.getId(), animal.getId());        // Arrange    void obterPlanoPorEstadiaDeveRetornarPlanoCriado() throws Exception {    @Test     */     * LAC-02: Obter plano ativo para uma estadia    /**    }        assertTrue(plano.getAtivo());        assertEquals(PrioridadePlano.ROTINA, plano.getPrioridade());        assertEquals(estadia.getId(), plano.getEstadiaId());        assertEquals(animal.getId(), plano.getAnimalId());        assertNotNull(plano.getId());        // Assert        PlanoCuidadosDto plano = planoCuidadosService.criarPlanoParaEstadia(estadia.getId(), animal.getId());        // Act    void criarPlanoDeveArmazenarComSucesso() throws Exception {    @Test     */     * LAC-02: Criar novo plano de cuidados para uma estadia    /**    }        estadia = estadiaRepository.save(estadia);        estadia.setDataFim(LocalDateTime.now().plusDays(5));        estadia.setDataInicio(LocalDateTime.now());        estadia.setAnimal(animal);        estadia.setReserva(reserva);        estadia = new Estadia();        reserva = reservaRepository.save(reserva);        reserva.setDataCheckIn(LocalDateTime.now());        Reserva reserva = new Reserva();        // Criar reserva e estadia        animal = animalRepository.save(animal);        animal.setNome("Rex");        animal = new Animal();        // Criar animal    void setup() {    @BeforeEach    private Estadia estadia;    private Animal animal;    private PlanoCuidadosRepository planoCuidadosRepository;    @Autowired    private ReservaRepository reservaRepository;    @Autowired    private EstadiaRepository estadiaRepository;    @Autowired    private AnimalRepository animalRepository;    @Autowired    private IPlanoCuidadosService planoCuidadosService;    @Autowiredclass PlanoCuidadosServiceIntegrationTest {@Transactional@SpringBootTest */ * LAC-02: Validação de funcionalidades de plano de cuidados e hook de escalação * Testes de integração para PlanoCuidadosService/**import static org.junit.jupiter.api.Assertions.*;import java.time.LocalDateTime;import pt.hotel.animais.repository.PlanoCuidadosRepository;import pt.hotel.animais.repository.ReservaRepository;import pt.hotel.animais.repository.EstadiaRepository;import pt.hotel.animais.repository.AnimalRepository;import pt.hotel.animais.model.enums.PeriodicidadeTarefa;import pt.hotel.animais.model.enums.PrioridadePlano;import pt.hotel.animais.model.Reserva;import pt.hotel.animais.model.Estadia;import pt.hotel.animais.model.Animal;import pt.hotel.animais.dto.TarefaCuidadoFormDto;import pt.hotel.animais.dto.PlanoCuidadosDto;import org.springframework.transaction.annotation.Transactional;import org.springframework.data.domain.PageRequest;import org.springframework.boot.test.context.SpringBootTest;import org.springframework.beans.factory.annotation.Autowired;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.BeforeEach;import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PlanoCuidadosController.class)
@Import(SecurityConfig.class)
class PlanoCuidadosControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IPlanoCuidadosService planoCuidadosService;

    /**
     * LAC-02: Teste para visualizar plano ativo de uma estadia
     */
    @Test
    @WithMockUser(roles = {"CUIDADOR"})
    void viewPlanoDeveRenderizarPlanoDaEstadia() throws Exception {
        // Arrange
        PlanoCuidadosDto dto = new PlanoCuidadosDto();
        dto.setId(1L);
        dto.setEstadiaId(1L);
        dto.setAnimalId(1L);
        dto.setDataInicio(LocalDateTime.now());
        dto.setDataFim(LocalDateTime.now().plusDays(5));
        dto.setPrioridade(PrioridadePlano.ROTINA);
        dto.setAtivo(true);
        dto.setInstrucoes("Instruções iniciais");
        dto.setTarefas(new ArrayList<>());

        when(planoCuidadosService.obterPlanoPorEstadia(1L)).thenReturn(dto);

        // Act & Assert
        mvc.perform(get("/plano-cuidados").param("estadiaId", "1"))
            .andExpect(status().isOk())
            .andExpect(view().name("cuidados/plano"))
            .andExpect(model().attributeExists("plano"))
            .andExpect(model().attribute("estadiaId", 1L))
            .andExpect(model().attributeExists("prioridades"));
    }

    /**
     * LAC-02: Teste para criar novo plano de cuidados
     */
    @Test
    @WithMockUser(roles = {"VETERINARIO"})
    void criarPlanoDeveRedirecionarParaPlanoCriado() throws Exception {
        // Arrange
        PlanoCuidadosDto dto = new PlanoCuidadosDto();
        dto.setId(1L);
        when(planoCuidadosService.criarPlanoParaEstadia(1L, 1L)).thenReturn(dto);

        // Act & Assert
        mvc.perform(post("/plano-cuidados/criar")
                .param("estadiaId", "1")
                .param("animalId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));
    }

    /**
     * LAC-02: Teste para adicionar tarefa ao plano
     */
    @Test
    @WithMockUser(roles = {"CUIDADOR"})
    void adicionarTarefaDeveRedirecionarComSucesso() throws Exception {
        // Arrange
        TarefaCuidadoDto tarefaDto = new TarefaCuidadoDto();
        tarefaDto.setId(1L);
        when(planoCuidadosService.adicionarTarefa(anyLong(), any(), anyLong()))
            .thenReturn(tarefaDto);

        // Act & Assert
        mvc.perform(post("/plano-cuidados/1/tarefa")
                .param("tipo", "ALIMENTACAO")
                .param("descricao", "Alimentar animal")
                .param("periodicidade", "DIARIA")
                .param("dataHora", "2026-05-26T10:00")
                .param("estadiaId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));
    }

    /**
     * LAC-02: Teste para marcar tarefa como concluída
     */
    @Test
    @WithMockUser(roles = {"CUIDADOR"})
    void marcarTarefaConcluida() throws Exception {
        // Arrange
        doNothing().when(planoCuidadosService).marcarTarefaConcluida(anyLong(), anyLong());

        // Act & Assert
        mvc.perform(post("/plano-cuidados/tarefa/1/concluir")
                .param("estadiaId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));
    }

    /**
     * LAC-02: Teste para adicionar instruções ao plano
     */
    @Test
    @WithMockUser(roles = {"VETERINARIO"})
    void adicionarInstrucoesAoPlano() throws Exception {
        // Arrange
        doNothing().when(planoCuidadosService).adicionarInstrucoes(anyLong(), any(), anyLong());

        // Act & Assert
        mvc.perform(post("/plano-cuidados/1/instrucoes")
                .param("instrucoes", "Nova instrução")
                .param("estadiaId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));
    }

    /**
     * LAC-02: Teste para atualizar prioridade do plano (hook de escalação)
     */
    @Test
    @WithMockUser(roles = {"VETERINARIO"})
    void atualizarPrioridadeParaCritico() throws Exception {
        // Arrange
        doNothing().when(planoCuidadosService).atualizarPrioridade(anyLong(), eq(PrioridadePlano.CRITICO), anyLong());

        // Act & Assert
        mvc.perform(post("/plano-cuidados/1/prioridade")
                .param("prioridade", "CRITICO")
                .param("estadiaId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/plano-cuidados?estadiaId=1"));
    }

    /**
     * LAC-02: Teste para listar histórico de planos do animal
     */
    @Test
    @WithMockUser(roles = {"CUIDADOR"})
    void historicoPlanosdoAnimal() throws Exception {
        // Arrange
        PlanoCuidadosDto plano = new PlanoCuidadosDto();
        plano.setId(1L);
        plano.setAnimalId(1L);
        
        var planosPage = new PageImpl<>(List.of(plano), PageRequest.of(0, 10), 1);
        when(planoCuidadosService.listarPlanosDoAnimal(1L, PageRequest.of(0, 10)))
            .thenReturn(planosPage);

        // Act & Assert
        mvc.perform(get("/plano-cuidados/animal/1/historico"))
            .andExpect(status().isOk())
            .andExpect(view().name("cuidados/historico"))
            .andExpect(model().attributeExists("planosPage"))
            .andExpect(model().attribute("animalId", 1L));
    }

    /**
     * LAC-02: Teste para encerrar plano (chamado no check-out)
     */
    @Test
    @WithMockUser(roles = {"VETERINARIO"})
    void encerrarPlano() throws Exception {
        // Arrange
        doNothing().when(planoCuidadosService).encerrarPlano(1L);

        // Act & Assert
        mvc.perform(post("/plano-cuidados/1/encerrar")
                .param("estadiaId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/estadias?estadiaId=1"));
    }
}
