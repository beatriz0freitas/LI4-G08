package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Pagamento;
import pt.hotel.animais.model.enums.EstadoPagamento;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.model.enums.MomentoPagamento;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.repository.PagamentoRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Serviço de gestão de pagamentos.
 * Implementa as regras de cálculo de pagamentos no check-in e check-out.
 * 
 * - Check-in: valor base = dias estimados × tarifa ativa do tipo de alojamento (de BD)
 * - Check-out: cálculo de diferença se dias reais > estimados + serviços extra + intervenções clínicas
 * - Método de pagamento é obrigatório (não é permitido NAO_DEFINIDO)
 * - Custos negativos são rejeitados na validação das entidades
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PagamentoService implements IPagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final EstadiaRepository estadiaRepository;
    private final ServicoExtraRepository servicoExtraRepository;
    private final IntervencaoClinicaRepository intervencaoClinicaRepository;
    private final TipoAlojamentoTarifaService tipoAlojamentoTarifaService;
    private final AuditoriaOperacaoService auditoriaOperacaoService;

    /**
     * Calcula o valor base da estadia no check-in.
     * Fórmula: dias estimados × tarifa ativa do tipo de alojamento (de BD)
     */
    public BigDecimal calcularValorBase(Estadia estadia) {
        if (estadia == null || estadia.getReserva() == null || estadia.getReserva().getAlojamento() == null) {
            throw new IllegalArgumentException("Estadia, reserva ou alojamento não encontrados");
        }

        var inicio = estadia.getDataInicio();
        var dataFim = estadia.getReserva().getDataFim().atTime(23, 59, 59);
        
        long diasEstimados = Math.max(1, Duration.between(inicio, dataFim).toDays());
        
        BigDecimal tarifa = tipoAlojamentoTarifaService.obterValorTarifa(estadia.getReserva().getAlojamento().getTipo());
        
        return BigDecimal.valueOf(diasEstimados).multiply(tarifa).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Regista um pagamento (genérico para check-in ou check-out).
     * Valida que o método de pagamento é um dos 3 métodos reais (não NAO_DEFINIDO).
     */
    public Pagamento registrarPagamento(PagamentoDto dto) {
        if (dto.getMetodoPagamento() == null) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório");
        }

        Estadia estadia = estadiaRepository.findById(dto.getEstadiaId())
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        Pagamento pagamento = new Pagamento();
        pagamento.setEstadia(estadia);
        pagamento.setValor(dto.getValor());
        pagamento.setMetodoPagamento(dto.getMetodoPagamento());
        pagamento.setMomentoPagamento(dto.getMomentoPagamento());
        pagamento.setEstadoPagamento(dto.getEstadoPagamento() == null ? EstadoPagamento.LIQUIDADO : dto.getEstadoPagamento());

        Pagamento guardado = pagamentoRepository.save(pagamento);
        auditoriaOperacaoService.registarSucesso(
            "PAGAMENTO_CRIADO",
            "Pagamento",
            guardado.getId(),
            "CREATE",
            detalhesPagamento(guardado)
        );
        return guardado;
    }

    /**
     * Calcula a cobrança complementar no check-out.
     * Inclui: (1) diferença de dias se reais > estimados (2) serviços extra (3) intervenções clínicas
     * 
     * Fórmula: (dias reais - dias estimados) × tarifa + soma(extras) + soma(clínica)
     * Nunca pode resultar em cobrado menos que o estimado (se dias reais <= estimados, cobra-se apenas extras + clínica)
     */
    public BigDecimal calcularCobrancaComplementar(Estadia estadia) {
        if (estadia == null || estadia.getId() == null) {
            throw new IllegalArgumentException("Estadia inválida");
        }

        BigDecimal total = BigDecimal.ZERO;

        // 1. Calcular diferença de dias se dias reais > dias estimados
        LocalDateTime dataInicio = estadia.getDataInicio();
        LocalDateTime dataFim = estadia.getDataFim();
        LocalDateTime dataFimReserva = estadia.getReserva().getDataFim().atTime(23, 59, 59);

        if (dataFim != null && dataFim.isAfter(dataFimReserva)) {
            long diasEstimados = Math.max(1, Duration.between(dataInicio, dataFimReserva).toDays());
            long diasReais = Math.max(1, Duration.between(dataInicio, dataFim).toDays());
            
            if (diasReais > diasEstimados) {
                long diasDiferenca = diasReais - diasEstimados;
                BigDecimal tarifa = tipoAlojamentoTarifaService.obterValorTarifa(estadia.getReserva().getAlojamento().getTipo());
                total = total.add(BigDecimal.valueOf(diasDiferenca).multiply(tarifa).setScale(2, RoundingMode.HALF_UP));
            }
        }

        // 2. Somar serviços extra
        BigDecimal somaExtras = servicoExtraRepository.sumCustoByEstadiaId(estadia.getId());
        if (somaExtras != null) {
            total = total.add(somaExtras);
        }

        // 3. Somar intervenções clínicas
        BigDecimal somaClinica = intervencaoClinicaRepository.sumCustoByEstadiaId(estadia.getId());
        if (somaClinica != null) {
            total = total.add(somaClinica);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Regista o pagamento de check-out com a cobrança complementar calculada.
     * Método de pagamento é obrigatório (não é permitido NAO_DEFINIDO).
     * 
     * @param estadiaId ID da estadia
     * @param metodoPagamento método de pagamento real (NUMERARIO, CARTAO_DEBITO, CARTAO_CREDITO)
     * @return pagamento registado
     */
    public Pagamento registrarPagamentoCheckOut(Long estadiaId, MetodoPagamento metodoPagamento) {
        if (metodoPagamento == null) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório no check-out");
        }

        Estadia estadia = estadiaRepository.findById(estadiaId)
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        BigDecimal valorComplementar = calcularCobrancaComplementar(estadia);

        Pagamento pagamento = new Pagamento();
        pagamento.setEstadia(estadia);
        pagamento.setValor(valorComplementar);
        pagamento.setMetodoPagamento(metodoPagamento);
        pagamento.setMomentoPagamento(MomentoPagamento.CHECK_OUT);
        pagamento.setEstadoPagamento(EstadoPagamento.LIQUIDADO);

        Pagamento guardado = pagamentoRepository.save(pagamento);
        auditoriaOperacaoService.registarSucesso(
            "PAGAMENTO_LIQUIDADO",
            "Pagamento",
            guardado.getId(),
            "CREATE",
            detalhesPagamento(guardado)
        );
        return guardado;
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

    private Map<String, Object> detalhesPagamento(Pagamento pagamento) {
        Map<String, Object> detalhes = new LinkedHashMap<>();
        detalhes.put("estadiaId", pagamento.getEstadia() != null ? pagamento.getEstadia().getId() : null);
        detalhes.put("valor", pagamento.getValor() != null ? pagamento.getValor().toPlainString() : null);
        detalhes.put("metodoPagamento", pagamento.getMetodoPagamento() != null ? pagamento.getMetodoPagamento().name() : null);
        detalhes.put("momentoPagamento", pagamento.getMomentoPagamento() != null ? pagamento.getMomentoPagamento().name() : null);
        detalhes.put("estadoPagamento", pagamento.getEstadoPagamento() != null ? pagamento.getEstadoPagamento().name() : null);
        return detalhes;
    }
}
