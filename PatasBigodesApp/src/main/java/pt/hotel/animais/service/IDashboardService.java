package pt.hotel.animais.service;

import java.math.BigDecimal;

public interface IDashboardService {
    long contarEstadiasAtivas();

    long contarReservasFuturas();

    BigDecimal faturacaoTotal();

    long contarAlojamentosPendentesLimpeza();

    long contarAlojamentosDisponiveis();

    long contarReservasAtivas();

    long contarPagamentosPendentes();
}
