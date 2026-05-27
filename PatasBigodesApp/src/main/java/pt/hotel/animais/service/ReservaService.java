package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.ReservaDetalheFinanceiroDto;
import pt.hotel.animais.dto.ReservaFormDto;
import pt.hotel.animais.dto.ReservaListDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.EstadoReserva;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.ReservaRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar reservas.
 * Implementa as regras de negócio para criar, modificar e cancelar reservas,
 * incluindo validação de disponibilidade e prevenção de overbooking.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService implements IReservaService {
    
    private final ReservaRepository reservaRepository;
    private final ITutorService tutorService;
    private final IAnimalService animalService;
    private final IAvailabilityDomainService availabilityDomainService;
    private final EstadiaRepository estadiaRepository;
    private final IPagamentoService pagamentoService;
    private final AuditoriaOperacaoService auditoriaOperacaoService;
    
    /**
     * Cria uma nova reserva com validações rigorosas.
     * 
     * Validações:
     * 1. Tutor, animal e alojamento existem
     * 2. Animal pertence ao tutor
     * 3. Data de fim > data de início
     * 4. Alojamento está disponível (limpo e sem conflitos)
     */
    public Reserva criar(ReservaFormDto formDto) {
        // Valida que tutor existe
        Tutor tutor = tutorService.obter(formDto.getTutorId());
        
        // Valida que animal existe
        Animal animal = animalService.obter(formDto.getAnimalId());
        
        // Valida que animal pertence ao tutor
        if (!animal.getTutor().getId().equals(tutor.getId())) {
            throw new IllegalArgumentException("O animal não pertence ao tutor especificado");
        }
        
        // Valida as datas
        if (formDto.getDataInicio() == null || formDto.getDataFim() == null) {
            throw new IllegalArgumentException("Datas de entrada obrigatórias");
        }
        
        if (!formDto.getDataInicio().isBefore(formDto.getDataFim())) {
            throw new IllegalArgumentException("Data de início deve ser anterior a data de fim");
        }
        
        // Verifica overbooking antes do lock para devolver erro explícito de reserva sobreposta.
        long conflitos = reservaRepository.countActiveInPeriod(
            formDto.getAlojamentoId(),
            formDto.getDataInicio(),
            formDto.getDataFim()
        );
        
        if (conflitos > 0) {
            throw new IllegalArgumentException(
                "O alojamento já tem reservas ativas ou confirmadas neste período. Conflito: overbooking não permitido."
            );
        }
        
        // Valida a regra completa de disponibilidade com lock pessimista no alojamento.
        Alojamento alojamento = availabilityDomainService.validarDisponivelParaReservaComLock(
            formDto.getAlojamentoId(),
            formDto.getDataInicio(),
            formDto.getDataFim(),
            animal.getEspecie()
        );
        
        // Cria a reserva
        Reserva reserva = new Reserva();
        reserva.setTutor(tutor);
        reserva.setAnimal(animal);
        reserva.setAlojamento(alojamento);
        reserva.setDataInicio(formDto.getDataInicio());
        reserva.setDataFim(formDto.getDataFim());
        reserva.setEstado(EstadoReserva.ATIVA);
        
        Reserva criada = reservaRepository.save(reserva);
        auditoriaOperacaoService.registarSucesso(
            "CRIAR_RESERVA",
            "Reserva",
            criada.getId(),
            "CREATE",
            detalhesReserva(criada)
        );
        return criada;
    }
    
    /**
     * Procura uma reserva pelo ID.
     */
    @Transactional(readOnly = true)
    public Reserva obter(Long id) {
        return reservaRepository.findWithDetalhesById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva com ID " + id + " não encontrada"));
    }

    @Transactional(readOnly = true)
    public Optional<ReservaDetalheFinanceiroDto> obterDetalheFinanceiro(Long id) {
        return estadiaRepository.findByReservaId(id)
            .map(estadia -> new ReservaDetalheFinanceiroDto(
                estadia.getId(),
                pagamentoService.calcularCobrancaComplementar(estadia)
            ));
    }
    
    /**
     * Procura todas as reservas de um tutor.
     */
    @Transactional(readOnly = true)
    public List<Reserva> procurarPorTutor(Long tutorId) {
        return reservaRepository.findByTutorId(tutorId);
    }
    
    /**
     * Procura todas as reservas de um animal.
     */
    @Transactional(readOnly = true)
    public List<Reserva> procurarPorAnimal(Long animalId) {
        return reservaRepository.findByAnimalId(animalId);
    }
    
    /**
     * Procura todas as reservas ativas de um tutor (ordenadas por data de criação, mais recentes primeiro).
     */
    @Transactional(readOnly = true)
    public List<Reserva> procurarAtivas(Long tutorId) {
        return reservaRepository.findByTutorIdOrderByDataCriacaoDesc(tutorId);
    }
    
    /**
     * Procura todas as reservas de um alojamento.
     */
    @Transactional(readOnly = true)
    public List<Reserva> procurarPorAlojamento(Long alojamentoId) {
        return reservaRepository.findByAlojamentoId(alojamentoId);
    }
    
    /**
     * Cancela uma reserva.
     * Uma reserva cancelada não pode ser reativada (regra de negócio).
     */
    public Reserva cancelar(Long id) {
        Reserva reserva = obter(id);
        
        if (!reserva.podeSerCancelada()) {
            throw new IllegalArgumentException(
                "Reserva não pode ser cancelada no estado: " + reserva.getEstado()
            );
        }
        
        reserva.setEstado(EstadoReserva.CANCELADA);
        Reserva cancelada = reservaRepository.save(reserva);
        auditoriaOperacaoService.registarSucesso(
            "CANCELAR_RESERVA",
            "Reserva",
            cancelada.getId(),
            "UPDATE",
            Map.of("estadoNovo", EstadoReserva.CANCELADA.name())
        );
        return cancelada;
    }
    
    /**
     * Confirma uma reserva ativa durante o check-in.
     */
    public Reserva confirmar(Long id) {
        Reserva reserva = obter(id);

        if (reserva.getEstado() != EstadoReserva.ATIVA) {
            throw new IllegalArgumentException(
                "Apenas reservas ativas podem ser confirmadas"
            );
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    /**
     * Marca uma reserva confirmada como concluída após check-out da estadia.
     */
    public Reserva concluir(Long id) {
        Reserva reserva = obter(id);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new IllegalArgumentException(
                "Apenas reservas confirmadas podem ser concluídas após check-out"
            );
        }

        reserva.setEstado(EstadoReserva.CONCLUIDA);
        return reservaRepository.save(reserva);
    }

    /**
     * Conta as reservas ativas.
     */
    @Transactional(readOnly = true)
    public long contarReservasAtivas() {
        return reservaRepository.countByEstado(EstadoReserva.ATIVA);
    }

    /**
     * Conta reservas ativas com início futuro.
     */
    @Transactional(readOnly = true)
    public long contarReservasFuturas() {
        return reservaRepository.countFuturas(LocalDate.now());
    }
    
    /**
     * Lista todas as reservas.
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarTodas() {
        return reservaRepository.findAllWithDetalhes();
    }

    /**
     * Lista reservas com filtro opcional por estado.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReservaListDto> listarComFiltros(EstadoReserva estado) {
        List<Reserva> reservas;
        
        if (estado != null) {
            reservas = reservaRepository.findByEstadoOrderByDataInicioDesc(estado);
        } else {
            reservas = reservaRepository.findAllWithDetalhes();
        }
        
        return reservas.stream()
            .map(this::converterParaDto)
            .collect(Collectors.toList());
    }

    private ReservaListDto converterParaDto(Reserva reserva) {
        String estadoLabel = obterLabelEstado(reserva.getEstado());
        String estadoCss = obterClassCssEstado(reserva.getEstado());
        
        return new ReservaListDto(
            reserva.getId(),
            reserva.getAnimal() != null ? reserva.getAnimal().getNome() : "-",
            reserva.getTutor() != null ? reserva.getTutor().getNome() : "-",
            reserva.getAlojamento() != null ? reserva.getAlojamento().getIdentificacao() : "-",
            reserva.getDataInicio(),
            reserva.getDataFim(),
            reserva.getEstado(),
            estadoLabel,
            estadoCss
        );
    }

    private String obterLabelEstado(EstadoReserva estado) {
        return switch (estado) {
            case ATIVA -> "Ativa";
            case CONFIRMADA -> "Confirmada";
            case CANCELADA -> "Cancelada";
            case CONCLUIDA -> "Concluída";
            default -> "Desconhecido";
        };
    }

    private String obterClassCssEstado(EstadoReserva estado) {
        return switch (estado) {
            case ATIVA -> "st-reservado";
            case CONFIRMADA -> "st-ocupado";
            case CANCELADA -> "st-limpeza";
            case CONCLUIDA -> "st-livre";
            default -> "";
        };
    }

    private Map<String, Object> detalhesReserva(Reserva reserva) {
        Map<String, Object> detalhes = new LinkedHashMap<>();
        detalhes.put("tutorId", reserva.getTutor() != null ? reserva.getTutor().getId() : null);
        detalhes.put("animalId", reserva.getAnimal() != null ? reserva.getAnimal().getId() : null);
        detalhes.put("alojamentoId", reserva.getAlojamento() != null ? reserva.getAlojamento().getId() : null);
        detalhes.put("dataInicio", reserva.getDataInicio() != null ? reserva.getDataInicio().toString() : null);
        detalhes.put("dataFim", reserva.getDataFim() != null ? reserva.getDataFim().toString() : null);
        detalhes.put("estado", reserva.getEstado() != null ? reserva.getEstado().name() : null);
        return detalhes;
    }
}
