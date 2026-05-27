package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.EstadiaListDto;
import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.dto.ResumoCheckInDto;
import pt.hotel.animais.dto.ResumoCheckOutDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.EstadoPagamento;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.model.enums.MomentoPagamento;
import pt.hotel.animais.repository.AnimalRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para gestão do ciclo de vida da estadia.
 * Check-out é transacional (ACID): se qualquer passo falhar, tudo é revertido.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EstadiaService implements IEstadiaService {

    private final EstadiaRepository estadiaRepository;
    private final IReservaService reservaService;
    private final IPagamentoService pagamentoService;
    private final IAlojamentoService alojamentoService;
    private final IPlanoCuidadosService planoCuidadosService;
    private final AnimalRepository animalRepository;
    private final AuditoriaOperacaoService auditoriaOperacaoService;

    /**
     * Abre uma estadia a partir de uma reserva ativa.
     * Confirma a reserva, cria estadia e regista pagamento base na mesma transação.
     */
    public Estadia abrirEstadiaPorReserva(Long reservaId, MetodoPagamento metodoPagamento) {
        if (metodoPagamento == null) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório no check-in");
        }

        Reserva reserva = reservaService.obter(reservaId);

        if (!reserva.podeFazerCheckIn()) {
            throw new IllegalArgumentException("Reserva não está em estado válido para check-in");
        }

        Long animalId = reserva.getAnimal().getId();
        animalRepository.findByIdForUpdate(animalId)
            .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado"));
        estadiaRepository.findEmCursoPorAnimal(animalId).ifPresent(estadiaExistente -> {
            throw new EstadiaExistenteException(
                "O animal já tem uma estadia em curso. Termine a estadia atual antes de registar novo check-in."
            );
        });

        reserva = reservaService.confirmar(reservaId);

        Estadia estadia = new Estadia();
        estadia.setReserva(reserva);
        estadia.setDataInicio(LocalDateTime.now());
        estadia.setEstado(EstadoEstadia.EM_CURSO);

        Estadia saved = estadiaRepository.save(estadia);
        planoCuidadosService.obterOuCriarPlanoParaEstadiaAtiva(saved.getId());

        // Criar pagamento base no check-in (valor calculado automaticamente)
        BigDecimal valorBase = pagamentoService.calcularValorBase(saved);
        PagamentoDto pagamentoDto = new PagamentoDto();
        pagamentoDto.setEstadiaId(saved.getId());
        pagamentoDto.setValor(valorBase);
        pagamentoDto.setMetodoPagamento(metodoPagamento);
        pagamentoDto.setMomentoPagamento(MomentoPagamento.CHECK_IN);
        pagamentoDto.setEstadoPagamento(EstadoPagamento.LIQUIDADO);

        pagamentoService.registrarPagamento(pagamentoDto);

        auditoriaOperacaoService.registarSucesso(
            "CHECK_IN",
            "Estadia",
            saved.getId(),
            "CREATE",
            detalhesCheckIn(saved, metodoPagamento, valorBase)
        );

        return saved;
    }

    /**
    * Check-out transacional para pagamento e fecho da estadia.
     * 
    * Sequência obrigatória para o fecho operacional:
     * 1. Validar que método de pagamento é real (obrigatório)
     * 2. Definir dataFim da estadia
     * 3. Calcular cobrança complementar
     * 4. Registar pagamento de check-out
    * 5. Mudar estado para TERMINADA
    * 6. Concluir a reserva associada
    * 7. Marcar alojamento para limpeza
     * 
    * Se qualquer passo falhar, a transação é revertida.
     */
    @Transactional
    public Estadia checkOut(Long estadiaId, MetodoPagamento metodoPagamento) {
        if (metodoPagamento == null) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório no check-out");
        }

        Estadia estadia = estadiaRepository.findById(estadiaId)
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        if (estadia.getEstado() != EstadoEstadia.EM_CURSO) {
            throw new IllegalArgumentException("Estadia não está em curso");
        }

        // Passo 1 + 2: Definir dataFim (validação de método já feita acima)
        estadia.setDataFim(LocalDateTime.now());

        // Passo 3 + 4: Calcular e registar pagamento de check-out
        // O pagamento é registado mesmo que seja zero (para auditoria)
        pagamentoService.registrarPagamentoCheckOut(estadiaId, metodoPagamento);

        // Passo 5: Marcar como TERMINADA
        estadia.setEstado(EstadoEstadia.TERMINADA);
        Estadia saved = estadiaRepository.save(estadia);
        planoCuidadosService.encerrarPlanoDaEstadia(saved.getId());

        // Passo 6: Concluir reserva associada antes da limpeza
        var reserva = estadia.getReserva();
        if (reserva != null) {
            reservaService.concluir(reserva.getId());
        }

        // Passo 7: A limpeza é parte da mesma transação
        if (reserva != null && reserva.getAlojamento() != null) {
            alojamentoService.marcarPendenteLimpeza(reserva.getAlojamento().getId());
        }

        auditoriaOperacaoService.registarSucesso(
            "CHECK_OUT",
            "Estadia",
            saved.getId(),
            "UPDATE",
            detalhesCheckOut(saved, metodoPagamento)
        );

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estadia> obterComDetalhes(Long estadiaId) {
        if (estadiaId == null) {
            return Optional.empty();
        }
        return estadiaRepository.findByIdComDetalhes(estadiaId);
    }

    @Transactional(readOnly = true)
    public Optional<ResumoCheckInDto> obterResumoCheckIn(Long reservaId) {
        if (reservaId == null) {
            return Optional.empty();
        }

        try {
            Reserva reserva = reservaService.obter(reservaId);
            Estadia estadiaPrevista = new Estadia();
            estadiaPrevista.setReserva(reserva);
            estadiaPrevista.setDataInicio(LocalDateTime.now());

            return Optional.of(new ResumoCheckInDto(
                reserva,
                pagamentoService.calcularValorBase(estadiaPrevista)
            ));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public Optional<ResumoCheckOutDto> obterResumoCheckOut(Long estadiaId) {
        if (estadiaId == null) {
            return Optional.empty();
        }

        return estadiaRepository.findByIdComDetalhes(estadiaId)
            .map(estadia -> new ResumoCheckOutDto(
                estadia,
                pagamentoService.calcularCobrancaComplementar(estadia)
            ));
    }

    /**
     * Conta as estadias em curso.
     */
    @Transactional(readOnly = true)
    public long contarEstadiasEmCurso() {
        return estadiaRepository.countByEstado(EstadoEstadia.EM_CURSO);
    }

    /**
     * Lista estadias com filtros opcionais.
     */
    @Override
    @Transactional(readOnly = true)
    public List<EstadiaListDto> listarComFiltros(EstadoEstadia estado, LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime dataInicioDateTime = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime dataFimDateTime = dataFim != null ? dataFim.atStartOfDay() : null;
        
        List<Estadia> estadias = estadiaRepository.findComFiltros(estado, dataInicioDateTime, dataFimDateTime);
        
        return estadias.stream()
            .map(this::converterParaDto)
            .collect(Collectors.toList());
    }

    private EstadiaListDto converterParaDto(Estadia estadia) {
        String estadoLabel = obterLabelEstado(estadia.getEstado());
        String estadoCss = obterClassCssEstado(estadia.getEstado());
        
        // Calcular duração
        LocalDateTime fimCalculo = estadia.getDataFim() != null ? estadia.getDataFim() : LocalDateTime.now();
        long dias = ChronoUnit.DAYS.between(estadia.getDataInicio(), fimCalculo);
        long horas = ChronoUnit.HOURS.between(estadia.getDataInicio(), fimCalculo) % 24;
        String duracao = String.format("%d dia(s), %d hora(s)", dias, horas);
        
        return new EstadiaListDto(
            estadia.getId(),
            estadia.getReserva() != null ? estadia.getReserva().getId() : null,
            estadia.getReserva() != null && estadia.getReserva().getAnimal() != null ? 
                estadia.getReserva().getAnimal().getNome() : "-",
            estadia.getReserva() != null && estadia.getReserva().getAlojamento() != null ? 
                estadia.getReserva().getAlojamento().getIdentificacao() : "-",
            estadia.getDataInicio(),
            estadia.getDataFim(),
            duracao,
            estadia.getEstado(),
            estadoLabel,
            estadoCss
        );
    }

    private String obterLabelEstado(EstadoEstadia estado) {
        return switch (estado) {
            case EM_CURSO -> "Em curso";
            case TERMINADA -> "Terminada";
            default -> "Desconhecido";
        };
    }

    private String obterClassCssEstado(EstadoEstadia estado) {
        return switch (estado) {
            case EM_CURSO -> "st-ocupado";
            case TERMINADA -> "st-livre";
            default -> "";
        };
    }

    private Map<String, Object> detalhesCheckIn(Estadia estadia,
                                                MetodoPagamento metodoPagamento,
                                                BigDecimal valorBase) {
        Map<String, Object> detalhes = new LinkedHashMap<>();
        detalhes.put("reservaId", estadia.getReserva() != null ? estadia.getReserva().getId() : null);
        detalhes.put("metodoPagamento", metodoPagamento != null ? metodoPagamento.name() : null);
        detalhes.put("valorBase", valorBase != null ? valorBase.toPlainString() : null);
        detalhes.put("estado", estadia.getEstado() != null ? estadia.getEstado().name() : null);
        return detalhes;
    }

    private Map<String, Object> detalhesCheckOut(Estadia estadia, MetodoPagamento metodoPagamento) {
        Map<String, Object> detalhes = new LinkedHashMap<>();
        detalhes.put("reservaId", estadia.getReserva() != null ? estadia.getReserva().getId() : null);
        detalhes.put("metodoPagamento", metodoPagamento != null ? metodoPagamento.name() : null);
        detalhes.put("dataFim", estadia.getDataFim() != null ? estadia.getDataFim().toString() : null);
        detalhes.put("estado", estadia.getEstado() != null ? estadia.getEstado().name() : null);
        return detalhes;
    }
}
