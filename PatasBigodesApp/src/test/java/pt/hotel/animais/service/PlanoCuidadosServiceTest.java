package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pt.hotel.animais.dto.PlanoCuidadosDto;
import pt.hotel.animais.dto.TarefaCuidadoDto;
import pt.hotel.animais.dto.TarefaCuidadoFormDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.PlanoCuidados;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.TarefaCuidado;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.PeriodicidadeTarefa;
import pt.hotel.animais.model.enums.PrioridadePlano;
import pt.hotel.animais.repository.AnimalRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.NotaRepository;
import pt.hotel.animais.repository.PlanoCuidadosRepository;
import pt.hotel.animais.repository.TarefaCuidadoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoCuidadosServiceTest {

    @Mock private PlanoCuidadosRepository planoCuidadosRepository;
    @Mock private TarefaCuidadoRepository tarefaCuidadoRepository;
    @Mock private EstadiaRepository estadiaRepository;
    @Mock private AnimalRepository animalRepository;
    @Mock private NotaRepository notaRepository;

    @InjectMocks
    private PlanoCuidadosService service;

    // ── criarPlanoParaEstadia ──────────────────────────────────────────────

    @Test
    void criarPlanoDeveAssociarAnimalEEstadiaComPrioridadeRotina() throws Exception {
        Estadia estadia = criarEstadia(1L);
        Animal animal = criarAnimal(2L);

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(planoCuidadosRepository.findByEstadiaId(1L)).thenReturn(Optional.empty());
        when(planoCuidadosRepository.save(any())).thenAnswer(inv -> {
            PlanoCuidados p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });
        when(tarefaCuidadoRepository.findByPlanoCuidadosId(10L)).thenReturn(List.of());

        PlanoCuidadosDto dto = service.criarPlanoParaEstadia(1L, 2L);

        assertThat(dto.getAnimalId()).isEqualTo(2L);
        assertThat(dto.getEstadiaId()).isEqualTo(1L);
        assertThat(dto.getPrioridade()).isEqualTo(PrioridadePlano.ROTINA);
        assertThat(dto.getAtivo()).isTrue();
        assertThat(dto.getDataInicio()).isNotNull();
        verify(planoCuidadosRepository).save(any(PlanoCuidados.class));
    }

    @Test
    void criarPlanoDeveLancarExcecaoSeJaExistePlanoParaEstadia() {
        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(criarEstadia(1L)));
        when(planoCuidadosRepository.findByEstadiaId(1L)).thenReturn(Optional.of(new PlanoCuidados()));

        assertThatThrownBy(() -> service.criarPlanoParaEstadia(1L, 2L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Já existe");

        verify(planoCuidadosRepository, never()).save(any());
    }

    @Test
    void criarPlanoDeveLancarExcecaoSeEstadiaInexistente() {
        when(estadiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criarPlanoParaEstadia(99L, 1L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Estadia");
    }

    @Test
    void criarPlanoDeveRejeitarAnimalDiferenteDoAssociadoAEstadia() {
        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(criarEstadia(1L)));

        assertThatThrownBy(() -> service.criarPlanoParaEstadia(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não corresponde");
    }

    @Test
    void obterOuCriarPlanoDeveDerivarAnimalEInstrucoesDaEstadiaAtiva() {
        Estadia estadia = criarEstadia(1L);
        Animal animal = estadia.getReserva().getAnimal();
        animal.setNecessidadesAlimentares("Ração hipoalergénica");
        animal.setMedicacaoCurso("Comprimido diário");
        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(planoCuidadosRepository.findByEstadiaId(1L)).thenReturn(Optional.empty());
        when(planoCuidadosRepository.save(any())).thenAnswer(inv -> {
            PlanoCuidados plano = inv.getArgument(0);
            plano.setId(10L);
            return plano;
        });
        when(tarefaCuidadoRepository.findByPlanoCuidadosId(10L)).thenReturn(List.of());

        PlanoCuidadosDto dto = service.obterOuCriarPlanoParaEstadiaAtiva(1L);

        assertThat(dto.getAnimalId()).isEqualTo(2L);
        assertThat(dto.getInstrucoes()).contains("Ração hipoalergénica", "Comprimido diário");
    }

    // ── obterPlanoPorEstadia ──────────────────────────────────────────────

    @Test
    void obterPlanoPorEstadiaDeveRetornarDtoCorreto() throws Exception {
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), criarAnimal(2L));
        when(planoCuidadosRepository.findByEstadiaId(1L)).thenReturn(Optional.of(plano));
        when(tarefaCuidadoRepository.findByPlanoCuidadosId(5L)).thenReturn(List.of());

        PlanoCuidadosDto dto = service.obterPlanoPorEstadia(1L);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getEstadiaId()).isEqualTo(1L);
    }

    @Test
    void obterPlanoPorEstadiaDeveLancarExcecaoSeNaoExiste() {
        when(planoCuidadosRepository.findByEstadiaId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obterPlanoPorEstadia(99L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void listarPlanosAtivosDeveRetornarPaginaParaSelecaoOperacional() {
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), criarAnimal(2L));
        when(planoCuidadosRepository.findByAtivoTrueOrderByDataInicioAsc(any()))
                .thenReturn(new PageImpl<>(List.of(plano)));
        when(tarefaCuidadoRepository.findByPlanoCuidadosId(5L)).thenReturn(List.of());

        Page<PlanoCuidadosDto> resultado = service.listarPlanosAtivos(PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getEstadiaId()).isEqualTo(1L);
    }

    @Test
    void listarTurnoDeveCriarPlanoEmFaltaParaEstadiaAtiva() {
        Estadia estadia = criarEstadia(1L);
        PlanoCuidados plano = criarPlano(5L, estadia, estadia.getReserva().getAnimal());
        PlanoCuidados planoDeEstadiaTerminada = criarPlano(6L, criarEstadia(9L), criarAnimal(3L));
        when(estadiaRepository.findEstadiasEmCursoComDetalhes()).thenReturn(List.of(estadia));
        when(planoCuidadosRepository.findByEstadiaId(1L)).thenReturn(Optional.empty());
        when(planoCuidadosRepository.save(any())).thenReturn(plano);
        when(planoCuidadosRepository.findByAtivoTrueOrderByDataInicioAsc())
            .thenReturn(List.of(plano, planoDeEstadiaTerminada));
        when(tarefaCuidadoRepository.findByPlanoCuidadosId(5L)).thenReturn(List.of());

        List<PlanoCuidadosDto> turno = service.listarPlanosAtivosDoTurno();

        assertThat(turno).hasSize(1);
        assertThat(turno.get(0).getEstadiaId()).isEqualTo(1L);
        verify(planoCuidadosRepository).save(any(PlanoCuidados.class));
    }

    // ── listarPlanosDoAnimal ──────────────────────────────────────────────

    @Test
    void listarPlanosDoAnimalDeveRetornarPaginaComPlanos() throws Exception {
        Animal animal = criarAnimal(2L);
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), animal);

        when(animalRepository.findById(2L)).thenReturn(Optional.of(animal));
        when(planoCuidadosRepository.findByAnimalIdOrderByDataInicio(eq(2L), any()))
                .thenReturn(new PageImpl<>(List.of(plano)));
        when(tarefaCuidadoRepository.findByPlanoCuidadosId(5L)).thenReturn(List.of());

        Page<PlanoCuidadosDto> resultado = service.listarPlanosDoAnimal(2L, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getAnimalId()).isEqualTo(2L);
    }

    @Test
    void listarPlanosDoAnimalDeveLancarExcecaoSeAnimalInexistente() {
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listarPlanosDoAnimal(99L, PageRequest.of(0, 10)))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Animal");
    }

    // ── adicionarTarefa ───────────────────────────────────────────────────

    @Test
    void adicionarTarefaDeveAssociarAoPlanoERetornarDto() throws Exception {
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), criarAnimal(2L));
        TarefaCuidadoFormDto form = criarTarefaForm("Banho", PeriodicidadeTarefa.DIARIA);

        when(planoCuidadosRepository.findById(5L)).thenReturn(Optional.of(plano));
        when(tarefaCuidadoRepository.save(any())).thenAnswer(inv -> {
            TarefaCuidado t = inv.getArgument(0);
            t.setId(20L);
            return t;
        });

        TarefaCuidadoDto dto = service.adicionarTarefa(5L, form, 1L);

        assertThat(dto.getId()).isEqualTo(20L);
        assertThat(dto.getDescricao()).isEqualTo("Banho");
        assertThat(dto.getPeriodicidade()).isEqualTo(PeriodicidadeTarefa.DIARIA);
        assertThat(dto.getConcluida()).isFalse();
        verify(tarefaCuidadoRepository).save(any(TarefaCuidado.class));
    }

    @Test
    void adicionarTarefaDeveLancarExcecaoSePlanoEncerrado() {
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), criarAnimal(2L));
        plano.setAtivo(false);

        when(planoCuidadosRepository.findById(5L)).thenReturn(Optional.of(plano));

        assertThatThrownBy(() -> service.adicionarTarefa(5L, criarTarefaForm("X", PeriodicidadeTarefa.UNICA), 1L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("encerrado");

        verify(tarefaCuidadoRepository, never()).save(any());
    }

    // ── marcarTarefaConcluida ─────────────────────────────────────────────

    @Test
    void marcarTarefaConcluidaDeveDefinirConcluidaEAutor() throws Exception {
        TarefaCuidado tarefa = new TarefaCuidado();
        tarefa.setId(20L);
        tarefa.setConcluida(false);
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), criarAnimal(2L));
        tarefa.setPlanoCuidados(plano);

        when(tarefaCuidadoRepository.findById(20L)).thenReturn(Optional.of(tarefa));

        service.marcarTarefaConcluida(20L, 7L);

        assertThat(tarefa.getConcluida()).isTrue();
        assertThat(tarefa.getAutorConclusaoId()).isEqualTo(7L);
        verify(tarefaCuidadoRepository).save(tarefa);
    }

    @Test
    void marcarTarefaConcluidaDeveLancarExcecaoSeTarefaInexistente() {
        when(tarefaCuidadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.marcarTarefaConcluida(99L, 1L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Tarefa");
    }

    // ── adicionarInstrucoes ───────────────────────────────────────────────

    @Test
    void adicionarInstrucoesSemInstrucoesAnteriorDeveDefinirTexto() throws Exception {
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), criarAnimal(2L));
        plano.setInstrucoes(null);

        when(planoCuidadosRepository.findById(5L)).thenReturn(Optional.of(plano));

        service.adicionarInstrucoes(5L, "Dar medicação às 8h", 1L);

        assertThat(plano.getInstrucoes()).isEqualTo("Dar medicação às 8h");
        verify(planoCuidadosRepository).save(plano);
    }

    @Test
    void adicionarInstrucoesComInstrucoesExistentesDeveConcatenar() throws Exception {
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), criarAnimal(2L));
        plano.setInstrucoes("Instrução anterior");

        when(planoCuidadosRepository.findById(5L)).thenReturn(Optional.of(plano));

        service.adicionarInstrucoes(5L, "Nova instrução", 1L);

        assertThat(plano.getInstrucoes()).contains("Instrução anterior");
        assertThat(plano.getInstrucoes()).contains("Nova instrução");
    }

    // ── atualizarPrioridade ───────────────────────────────────────────────

    @Test
    void atualizarPrioridadeDeveAlterarCampo() throws Exception {
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), criarAnimal(2L));
        plano.setPrioridade(PrioridadePlano.ROTINA);

        when(planoCuidadosRepository.findById(5L)).thenReturn(Optional.of(plano));

        service.atualizarPrioridade(5L, PrioridadePlano.CRITICO, 1L);

        assertThat(plano.getPrioridade()).isEqualTo(PrioridadePlano.CRITICO);
        verify(planoCuidadosRepository).save(plano);
    }

    // ── encerrarPlano ─────────────────────────────────────────────────────

    @Test
    void encerrarPlanoDeveDefinirAtivoFalsoEDataFim() throws Exception {
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), criarAnimal(2L));
        plano.setAtivo(true);

        when(planoCuidadosRepository.findById(5L)).thenReturn(Optional.of(plano));

        service.encerrarPlano(5L);

        assertThat(plano.getAtivo()).isFalse();
        assertThat(plano.getDataFim()).isNotNull();
        verify(planoCuidadosRepository).save(plano);
    }

    @Test
    void encerrarPlanoDeveLancarExcecaoSePlanoInexistente() {
        when(planoCuidadosRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.encerrarPlano(99L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Plano");
    }

    @Test
    void encerrarPlanoDaEstadiaDeveEncerrarPlanoAtivoQuandoExiste() {
        PlanoCuidados plano = criarPlano(5L, criarEstadia(1L), criarAnimal(2L));
        when(planoCuidadosRepository.findByEstadiaId(1L)).thenReturn(Optional.of(plano));

        service.encerrarPlanoDaEstadia(1L);

        assertThat(plano.getAtivo()).isFalse();
        verify(planoCuidadosRepository).save(plano);
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private Estadia criarEstadia(Long id) {
        Estadia e = new Estadia();
        e.setId(id);
        e.setDataInicio(LocalDateTime.now().minusDays(1));
        e.setEstado(EstadoEstadia.EM_CURSO);
        Reserva reserva = new Reserva();
        reserva.setAnimal(criarAnimal(2L));
        e.setReserva(reserva);
        return e;
    }

    private Animal criarAnimal(Long id) {
        Animal a = new Animal();
        a.setId(id);
        a.setNome("Animal " + id);
        return a;
    }

    private PlanoCuidados criarPlano(Long id, Estadia estadia, Animal animal) {
        PlanoCuidados p = new PlanoCuidados();
        p.setId(id);
        p.setEstadia(estadia);
        p.setAnimal(animal);
        p.setPrioridade(PrioridadePlano.ROTINA);
        p.setAtivo(true);
        p.setDataInicio(LocalDateTime.now().minusHours(1));
        p.setInstrucoes("");
        return p;
    }

    private TarefaCuidadoFormDto criarTarefaForm(String descricao, PeriodicidadeTarefa periodicidade) {
        TarefaCuidadoFormDto form = new TarefaCuidadoFormDto();
        form.setTipo("Higiene");
        form.setDescricao(descricao);
        form.setPeriodicidade(periodicidade);
        form.setDataHora(LocalDateTime.now());
        return form;
    }
}
