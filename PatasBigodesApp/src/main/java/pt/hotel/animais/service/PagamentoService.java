package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Pagamento;
import pt.hotel.animais.model.enums.EstadoPagamento;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.PagamentoRepository;

import java.math.BigDecimal;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class PagamentoService implements IPagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final EstadiaRepository estadiaRepository;

    public BigDecimal calcularValorBase(Estadia estadia) {
        // Placeholder: calcula dias * tarifa fixa (10.00 EUR)
        var inicio = estadia.getDataInicio();
        var fim = estadia.getDataFim() != null ? estadia.getDataFim() : inicio.plusDays(1);
        long dias = Math.max(1, Duration.between(inicio, fim).toDays());
        return BigDecimal.valueOf(dias).multiply(BigDecimal.valueOf(10.00));
    }

    public Pagamento registrarPagamento(PagamentoDto dto) {
        Estadia estadia = estadiaRepository.findById(dto.getEstadiaId())
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        Pagamento pagamento = new Pagamento();
        pagamento.setEstadia(estadia);
        pagamento.setValor(dto.getValor());
        pagamento.setMetodoPagamento(dto.getMetodoPagamento());
        pagamento.setMomentoPagamento(dto.getMomentoPagamento());
        pagamento.setEstadoPagamento(dto.getEstadoPagamento() == null ? EstadoPagamento.LIQUIDADO : dto.getEstadoPagamento());

        return pagamentoRepository.save(pagamento);
    }

    public Pagamento registrarPagamentoCheckOut(Long estadiaId, java.math.BigDecimal valor, MetodoPagamento metodoPagamento) {
        Estadia estadia = estadiaRepository.findById(estadiaId)
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        Pagamento pagamento = new Pagamento();
        pagamento.setEstadia(estadia);
        pagamento.setValor(valor);
        pagamento.setMetodoPagamento(metodoPagamento == null ? MetodoPagamento.NAO_DEFINIDO : metodoPagamento);
        pagamento.setMomentoPagamento(pt.hotel.animais.model.enums.MomentoPagamento.CHECK_OUT);
        pagamento.setEstadoPagamento(EstadoPagamento.LIQUIDADO);

        return pagamentoRepository.save(pagamento);
    }

    public java.math.BigDecimal calcularExtras(Estadia estadia) {
        // Placeholder: soma fictícia de extras (0.00)
        return java.math.BigDecimal.ZERO;
    }

    /**
     * Calcula a faturação total registada.
     */
    @Transactional(readOnly = true)
    public BigDecimal faturacaoTotal() {
        return pagamentoRepository.sumValorTotal();
    }

    /**
     * Conta pagamentos ainda não liquidados.
     */
    @Transactional(readOnly = true)
    public long contarPagamentosPendentes() {
        return pagamentoRepository.countPendentes();
    }
}
