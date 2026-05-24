package pt.hotel.animais.service;

import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Pagamento;
import pt.hotel.animais.model.enums.MetodoPagamento;

import java.math.BigDecimal;

public interface IPagamentoService {
    BigDecimal calcularValorBase(Estadia estadia);

    Pagamento registrarPagamento(PagamentoDto dto);

    Pagamento registrarPagamentoCheckOut(Long estadiaId, BigDecimal valor, MetodoPagamento metodoPagamento);

    BigDecimal calcularExtras(Estadia estadia);

    BigDecimal faturacaoTotal();

    long contarPagamentosPendentes();
}
