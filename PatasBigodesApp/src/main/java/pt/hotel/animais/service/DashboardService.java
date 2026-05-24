package pt.hotel.animais.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService implements IDashboardService {

    private final IReservaService reservaService;
    private final IEstadiaService estadiaService;
    private final IPagamentoService pagamentoService;
    private final IAlojamentoService alojamentoService;

    public DashboardService(IReservaService reservaService,
                            IEstadiaService estadiaService,
                            IPagamentoService pagamentoService,
                            IAlojamentoService alojamentoService) {
        this.reservaService = reservaService;
        this.estadiaService = estadiaService;
        this.pagamentoService = pagamentoService;
        this.alojamentoService = alojamentoService;
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
}
