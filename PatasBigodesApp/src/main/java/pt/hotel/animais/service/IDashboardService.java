package pt.hotel.animais.service;

import java.math.BigDecimal;
import java.util.List;

import pt.hotel.animais.dto.DashboardEstadiaDto;

public interface IDashboardService {
    long contarEstadiasAtivas();

    long contarReservasFuturas();

    BigDecimal faturacaoTotal();

    long contarAlojamentosPendentesLimpeza();

    long contarAlojamentosDisponiveis();

    long contarReservasAtivas();

    long contarPagamentosPendentes();

    long contarAlojamentosTotal();

    long contarAlojamentosOcupados();

    double taxaOcupacao();

    BigDecimal faturacaoMesAtual();

    List<DashboardEstadiaDto> listarEstadiasEmCurso();
}
