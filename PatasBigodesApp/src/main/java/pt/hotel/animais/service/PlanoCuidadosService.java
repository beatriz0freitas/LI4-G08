package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.PlanoCuidadosDto;
import pt.hotel.animais.dto.TarefaCuidadoDto;
import pt.hotel.animais.dto.TarefaCuidadoFormDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Nota;
import pt.hotel.animais.model.PlanoCuidados;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.TarefaCuidado;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.PrioridadePlano;
import pt.hotel.animais.repository.AnimalRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.NotaRepository;
import pt.hotel.animais.repository.PlanoCuidadosRepository;
import pt.hotel.animais.repository.TarefaCuidadoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de PlanoCuidados.
 * Suporta o duplo vínculo (Animal histórico + Estadia ativa)
 * e integração com hook de mudança de prioridade automática em casos críticos.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PlanoCuidadosService implements IPlanoCuidadosService {

    private final PlanoCuidadosRepository planoCuidadosRepository;
    private final TarefaCuidadoRepository tarefaCuidadoRepository;
    private final EstadiaRepository estadiaRepository;
    private final AnimalRepository animalRepository;
    private final NotaRepository notaRepository;

    @Override
    public PlanoCuidadosDto criarPlanoParaEstadia(Long estadiaId, Long animalId) throws Exception {
        log.info("Criando plano de cuidados para estadia {} e animal {}", estadiaId, animalId);

        Estadia estadia = obterEstadiaAtiva(estadiaId);
        Animal animal = estadia.getReserva().getAnimal();
        if (animalId == null || !animal.getId().equals(animalId)) {
            throw new IllegalArgumentException("O animal indicado não corresponde à estadia em curso");
        }

        if (planoCuidadosRepository.findByEstadiaId(estadiaId).isPresent()) {
            throw new Exception("Já existe um plano de cuidados ativo para esta estadia");
        }

        return criarNovoPlano(estadia, animal);
    }

    @Override
    public PlanoCuidadosDto obterOuCriarPlanoParaEstadiaAtiva(Long estadiaId) {
        Estadia estadia = obterEstadiaAtiva(estadiaId);
        return planoCuidadosRepository.findByEstadiaId(estadiaId)
            .map(this::mapearParaDto)
            .orElseGet(() -> criarNovoPlano(estadia, estadia.getReserva().getAnimal()));
    }

    private PlanoCuidadosDto criarNovoPlano(Estadia estadia, Animal animal) {
        PlanoCuidados plano = new PlanoCuidados();
        plano.setAnimal(animal);
        plano.setEstadia(estadia);
        plano.setDataInicio(estadia.getDataInicio());
        plano.setDataFim(estadia.getDataFim());
        plano.setPrioridade(PrioridadePlano.ROTINA);
        plano.setAtivo(true);
        plano.setInstrucoes(construirInstrucoesIniciais(estadia, animal));

        PlanoCuidados planoSalvo = planoCuidadosRepository.save(plano);
        log.info("Plano criado com sucesso: {}", planoSalvo.getId());

        return mapearParaDto(planoSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanoCuidadosDto obterPlanoPorEstadia(Long estadiaId) throws Exception {
        log.info("Obtendo plano de cuidados para estadia {}", estadiaId);

        PlanoCuidados plano = planoCuidadosRepository.findByEstadiaId(estadiaId)
                .orElseThrow(() -> new Exception("Plano de cuidados não encontrado para estadia: " + estadiaId));

        return mapearParaDto(plano);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlanoCuidadosDto> listarPlanosAtivos(Pageable pageable) {
        Page<PlanoCuidados> planosPage = planoCuidadosRepository.findByAtivoTrueOrderByDataInicioAsc(pageable);
        List<PlanoCuidadosDto> planosDto = planosPage.getContent()
                .stream()
                .map(this::mapearParaDto)
                .collect(Collectors.toList());

        return new PageImpl<>(planosDto, pageable, planosPage.getTotalElements());
    }

    @Override
    public List<PlanoCuidadosDto> listarPlanosAtivosDoTurno() {
        List<Estadia> estadiasAtivas = estadiaRepository.findEstadiasEmCursoComDetalhes();
        Set<Long> idsEstadiasAtivas = estadiasAtivas.stream()
            .map(Estadia::getId)
            .collect(Collectors.toSet());
        for (Estadia estadia : estadiasAtivas) {
            if (planoCuidadosRepository.findByEstadiaId(estadia.getId()).isEmpty()) {
                criarNovoPlano(estadia, estadia.getReserva().getAnimal());
            }
        }

        return planoCuidadosRepository.findByAtivoTrueOrderByDataInicioAsc().stream()
            .filter(plano -> idsEstadiasAtivas.contains(plano.getEstadia().getId()))
            .map(this::mapearParaDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlanoCuidadosDto> listarPlanosDoAnimal(Long animalId, Pageable pageable) throws Exception {
        log.info("Listando planos do animal {}", animalId);

        // Validar animal existe
        animalRepository.findById(animalId)
                .orElseThrow(() -> new Exception("Animal não encontrado: " + animalId));

        Page<PlanoCuidados> planosPage = planoCuidadosRepository.findByAnimalIdOrderByDataInicio(animalId, pageable);
        List<PlanoCuidadosDto> planosDto = planosPage.getContent()
                .stream()
                .map(this::mapearParaDto)
                .collect(Collectors.toList());

        return new PageImpl<>(planosDto, pageable, planosPage.getTotalElements());
    }

    @Override
    public TarefaCuidadoDto adicionarTarefa(Long planoCuidadosId, TarefaCuidadoFormDto formDto, Long autorId) throws Exception {
        log.info("Adicionando tarefa ao plano {}", planoCuidadosId);

        PlanoCuidados plano = planoCuidadosRepository.findById(planoCuidadosId)
                .orElseThrow(() -> new Exception("Plano não encontrado: " + planoCuidadosId));

        if (!plano.getAtivo()) {
            throw new Exception("Não é possível adicionar tarefas a um plano encerrado");
        }

        // Criar nova tarefa
        TarefaCuidado tarefa = new TarefaCuidado();
        tarefa.setPlanoCuidados(plano);
        tarefa.setTipo(formDto.getTipo());
        tarefa.setDescricao(formDto.getDescricao());
        tarefa.setPeriodicidade(formDto.getPeriodicidade());
        tarefa.setDataHora(formDto.getDataHora());
        tarefa.setConcluida(false);
        tarefa.setAutorConclusaoId(null);

        TarefaCuidado tarefaSalva = tarefaCuidadoRepository.save(tarefa);
        log.info("Tarefa criada com sucesso: {}", tarefaSalva.getId());

        return mapearTarefaParaDto(tarefaSalva);
    }

    @Override
    public void marcarTarefaConcluida(Long tarefaId, Long autorId) throws Exception {
        log.info("Marcando tarefa {} como concluída por autor {}", tarefaId, autorId);

        TarefaCuidado tarefa = tarefaCuidadoRepository.findById(tarefaId)
                .orElseThrow(() -> new Exception("Tarefa não encontrada: " + tarefaId));

        tarefa.setConcluida(true);
        tarefa.setAutorConclusaoId(autorId);

        tarefaCuidadoRepository.save(tarefa);
        log.info("Tarefa marcada como concluída: {}", tarefaId);
    }

    @Override
    public void adicionarInstrucoes(Long planoCuidadosId, String instrucoes, Long autorId) throws Exception {
        log.info("Adicionando instruções ao plano {}", planoCuidadosId);

        PlanoCuidados plano = planoCuidadosRepository.findById(planoCuidadosId)
                .orElseThrow(() -> new Exception("Plano não encontrado: " + planoCuidadosId));

        // Adicionar/atualizar instruções (concatenar se já existem)
        String novasInstrucoes = plano.getInstrucoes() == null ? instrucoes : 
                                 plano.getInstrucoes() + "\n\n" + instrucoes;
        plano.setInstrucoes(novasInstrucoes);

        planoCuidadosRepository.save(plano);
        log.info("Instruções adicionadas ao plano: {}", planoCuidadosId);
    }

    @Override
    public void atualizarPrioridade(Long planoCuidadosId, PrioridadePlano novaPrioridade, Long autorId) throws Exception {
        log.info("Atualizando prioridade do plano {} para {}", planoCuidadosId, novaPrioridade);

        PlanoCuidados plano = planoCuidadosRepository.findById(planoCuidadosId)
                .orElseThrow(() -> new Exception("Plano não encontrado: " + planoCuidadosId));

        plano.setPrioridade(novaPrioridade);
        planoCuidadosRepository.save(plano);
        log.info("Prioridade atualizada para plano: {} -> {}", planoCuidadosId, novaPrioridade);
    }

    @Override
    public void encerrarPlano(Long planoCuidadosId) throws Exception {
        log.info("Encerrando plano {}", planoCuidadosId);

        PlanoCuidados plano = planoCuidadosRepository.findById(planoCuidadosId)
                .orElseThrow(() -> new Exception("Plano não encontrado: " + planoCuidadosId));

        plano.setAtivo(false);
        plano.setDataFim(LocalDateTime.now());
        planoCuidadosRepository.save(plano);
        log.info("Plano encerrado: {}", planoCuidadosId);
    }

    @Override
    public void encerrarPlanoDaEstadia(Long estadiaId) {
        planoCuidadosRepository.findByEstadiaId(estadiaId).ifPresent(plano -> {
            if (Boolean.TRUE.equals(plano.getAtivo())) {
                plano.setAtivo(false);
                plano.setDataFim(LocalDateTime.now());
                planoCuidadosRepository.save(plano);
            }
        });
    }

    /**
     * Converter PlanoCuidados para PlanoCuidadosDto
     */
    private PlanoCuidadosDto mapearParaDto(PlanoCuidados plano) {
        List<TarefaCuidadoDto> tarefasDto = tarefaCuidadoRepository.findByPlanoCuidadosId(plano.getId())
                .stream()
                .map(this::mapearTarefaParaDto)
                .collect(Collectors.toList());

        PlanoCuidadosDto dto = new PlanoCuidadosDto();
        dto.setId(plano.getId());
        dto.setAnimalId(plano.getAnimal().getId());
        dto.setEstadiaId(plano.getEstadia().getId());
        dto.setDataInicio(plano.getDataInicio());
        dto.setDataFim(plano.getDataFim());
        dto.setPrioridade(plano.getPrioridade());
        dto.setAtivo(plano.getAtivo());
        dto.setInstrucoes(plano.getInstrucoes());
        dto.setTarefas(tarefasDto);
        Animal animal = plano.getAnimal();
        dto.setAnimalNome(animal.getNome());
        dto.setEspecieLabel(animal.getEspecie() != null ? animal.getEspecie().getLabel() : "");
        dto.setRaca(animal.getRaca());
        dto.setEstadoSaude(animal.getEstadoSaude() != null ? animal.getEstadoSaude().getLabel() : "");
        dto.setMedicacaoCurso(animal.getMedicacaoCurso());

        Reserva reserva = plano.getEstadia().getReserva();
        if (reserva != null) {
            if (reserva.getTutor() != null) {
                dto.setTutorNome(reserva.getTutor().getNome());
            } else if (animal.getTutor() != null) {
                dto.setTutorNome(animal.getTutor().getNome());
            }
            if (reserva.getAlojamento() != null) {
                dto.setAlojamentoIdentificacao(reserva.getAlojamento().getIdentificacao());
            }
        }

        return dto;
    }

    private Estadia obterEstadiaAtiva(Long estadiaId) {
        Estadia estadia = estadiaRepository.findById(estadiaId)
            .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada: " + estadiaId));
        if (estadia.getEstado() != EstadoEstadia.EM_CURSO) {
            throw new IllegalArgumentException("A estadia não está em curso");
        }
        if (estadia.getReserva() == null || estadia.getReserva().getAnimal() == null) {
            throw new IllegalArgumentException("A estadia não possui animal associado");
        }
        return estadia;
    }

    private String construirInstrucoesIniciais(Estadia estadia, Animal animal) {
        List<String> instrucoes = new ArrayList<>();
        if (animal.getNecessidadesAlimentares() != null && !animal.getNecessidadesAlimentares().isBlank()) {
            instrucoes.add("Necessidades alimentares: " + animal.getNecessidadesAlimentares());
        }
        if (animal.getMedicacaoCurso() != null && !animal.getMedicacaoCurso().isBlank()) {
            instrucoes.add("Medicação em curso: " + animal.getMedicacaoCurso());
        }
        if (estadia.getReserva() != null && estadia.getReserva().getId() != null) {
            notaRepository.findByReservaId(estadia.getReserva().getId()).stream()
                .map(Nota::getDescricao)
                .filter(descricao -> descricao != null && !descricao.isBlank())
                .forEach(descricao -> instrucoes.add("Nota da reserva: " + descricao));
        }
        return String.join("\n", instrucoes);
    }

    /**
     * Converter TarefaCuidado para TarefaCuidadoDto
     */
    private TarefaCuidadoDto mapearTarefaParaDto(TarefaCuidado tarefa) {
        TarefaCuidadoDto dto = new TarefaCuidadoDto();
        dto.setId(tarefa.getId());
        dto.setPlanoCuidadosId(tarefa.getPlanoCuidados().getId());
        dto.setTipo(tarefa.getTipo());
        dto.setDescricao(tarefa.getDescricao());
        dto.setPeriodicidade(tarefa.getPeriodicidade());
        dto.setDataHora(tarefa.getDataHora());
        dto.setConcluida(tarefa.getConcluida());
        dto.setAutorConclusaoId(tarefa.getAutorConclusaoId());

        return dto;
    }
}
