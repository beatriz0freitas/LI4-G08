package pt.hotel.animais.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.DashboardEstadiaDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.PagamentoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService implements IDashboardService {

    private final IReservaService reservaService;
    private final IEstadiaService estadiaService;
    private final IPagamentoService pagamentoService;
    private final IAlojamentoService alojamentoService;
    private final AlojamentoRepository alojamentoRepository;
    private final EstadiaRepository estadiaRepository;
    private final PagamentoRepository pagamentoRepository;

    public DashboardService(IReservaService reservaService,
                            IEstadiaService estadiaService,
                            IPagamentoService pagamentoService,
                            IAlojamentoService alojamentoService,
                            AlojamentoRepository alojamentoRepository,
                            EstadiaRepository estadiaRepository,
                            PagamentoRepository pagamentoRepository) {
        this.reservaService = reservaService;
        this.estadiaService = estadiaService;
        this.pagamentoService = pagamentoService;
        this.alojamentoService = alojamentoService;
        this.alojamentoRepository = alojamentoRepository;
        this.estadiaRepository = estadiaRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    public long contarEstadiasAtivas() {
        return estadiaService.contarEstadiasEmCurso();
    }

    public long contarReservasFuturas() {
        return reservaService.contarReservasFuturas();
    }

    public BigDecimal faturacaoTotal() {
        return pagamentoService.faturacaoTotal();
    }

    public long contarAlojamentosPendentesLimpeza() {
        return alojamentoService.contarAlojamentosPendentesLimpeza();
    }

    public long contarAlojamentosDisponiveis() {
        return alojamentoService.contarAlojamentosDisponiveis();
    }

    public long contarReservasAtivas() {
        return reservaService.contarReservasAtivas();
    }

    public long contarPagamentosPendentes() {
        return pagamentoService.contarPagamentosPendentes();
    }

    public long contarAlojamentosTotal() {
        return alojamentoRepository.count();
    }

    public long contarAlojamentosOcupados() {
        return estadiaRepository.countAlojamentosOcupadosAgora();
    }

    public double taxaOcupacao() {
        long total = contarAlojamentosTotal();
        if (total == 0) {
            return 0;
        }
        return (contarAlojamentosOcupados() * 100.0) / total;
    }

    public BigDecimal faturacaoMesAtual() {
        LocalDate hoje = LocalDate.now();
        return pagamentoRepository.sumValorPorPeriodo(
            hoje.withDayOfMonth(1).atStartOfDay(),
            hoje.atTime(LocalTime.MAX)
        );
    }

    public List<DashboardEstadiaDto> listarEstadiasEmCurso() {
        return estadiaRepository.findEstadiasEmCursoDashboard(org.springframework.data.domain.PageRequest.of(0, 6))
            .stream()
            .map(this::toDashboardEstadia)
            .toList();
    }

    private DashboardEstadiaDto toDashboardEstadia(Estadia estadia) {
        DashboardEstadiaDto dto = new DashboardEstadiaDto();
        var reserva = estadia.getReserva();
        dto.setId(estadia.getId());
        dto.setAnimal(reserva.getAnimal().getNome());
        dto.setEspecie(reserva.getAnimal().getEspecie().name());
        dto.setAlojamento(reserva.getAlojamento().getIdentificacao());
        dto.setDataEntrada(estadia.getDataInicio());
        dto.setDataSaidaPrevista(reserva.getDataFim());
        dto.setEstado(estadia.getEstado().name());
        dto.setCheckoutHoje(LocalDate.now().equals(reserva.getDataFim()));
        return dto;
    }
}
