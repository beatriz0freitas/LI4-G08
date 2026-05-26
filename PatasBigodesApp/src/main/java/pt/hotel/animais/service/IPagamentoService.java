package pt.hotel.animais.service;

import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Pagamento;
import pt.hotel.animais.model.enums.MetodoPagamento;

import java.math.BigDecimal;

public interface IPagamentoService {
    BigDecimal calcularValorBase(Estadia estadia);

    Pagamento registrarPagamento(PagamentoDto dto);

    /**
     * Registar pagamento de check-out com cobrança complementar automática.
     * A cobrança é calculada automaticamente pelo serviço.
     */
    Pagamento registrarPagamentoCheckOut(Long estadiaId, MetodoPagamento metodoPagamento);

    BigDecimal calcularCobrancaComplementar(Estadia estadia);

    BigDecimal faturacaoTotal();

    long contarPagamentosPendentes();
}
