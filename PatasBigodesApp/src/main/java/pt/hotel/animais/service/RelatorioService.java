package pt.hotel.animais.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.RelatorioFiltroFormDto;
import pt.hotel.animais.dto.RelatorioResumoDto;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.PagamentoRepository;
import pt.hotel.animais.repository.ReservaRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RelatorioService implements IRelatorioService {

    private final AlojamentoRepository alojamentoRepository;
    private final EstadiaRepository estadiaRepository;
    private final ReservaRepository reservaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ServicoExtraRepository servicoExtraRepository;

    public RelatorioService(AlojamentoRepository alojamentoRepository,
                            EstadiaRepository estadiaRepository,
                            ReservaRepository reservaRepository,
                            PagamentoRepository pagamentoRepository,
                            ServicoExtraRepository servicoExtraRepository) {
        this.alojamentoRepository = alojamentoRepository;
        this.estadiaRepository = estadiaRepository;
        this.reservaRepository = reservaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.servicoExtraRepository = servicoExtraRepository;
    }

    @Override
    public RelatorioResumoDto gerarRelatorio(RelatorioFiltroFormDto filtro) {
        validarPeriodo(filtro);
        LocalDateTime inicio = filtro.getDataInicio().atStartOfDay();
        LocalDateTime fim = filtro.getDataFim().atTime(LocalTime.MAX);

        RelatorioResumoDto resumo = new RelatorioResumoDto();
        long alojamentosTotal = filtro.getTipoAlojamento() == null
            ? alojamentoRepository.count()
            : alojamentoRepository.countByTipo(filtro.getTipoAlojamento());
        long ocupados = estadiaRepository.countAlojamentosOcupadosAgora();

        resumo.setAlojamentosTotal(alojamentosTotal);
        resumo.setAlojamentosOcupados(Math.min(ocupados, alojamentosTotal));
        resumo.setTaxaOcupacao(alojamentosTotal == 0 ? 0 : (resumo.getAlojamentosOcupados() * 100.0) / alojamentosTotal);
        resumo.setEstadiasCount(estadiaRepository.countSobrepostasPeriodo(inicio, fim));
        resumo.setReservasCount(reservaRepository.countInPeriod(filtro.getDataInicio(), filtro.getDataFim()));
        resumo.setFaturacaoTotal(pagamentoRepository.sumValorPorPeriodo(inicio, fim));
        resumo.setPagamentosPendentes(pagamentoRepository.countPendentesPorPeriodo(inicio, fim));
        resumo.setServicosExtraTotal(filtro.isIncluirServicosExtra()
            ? servicoExtraRepository.sumCustoPorPeriodo(inicio, fim)
            : BigDecimal.ZERO);
        resumo.setFaturacaoPorMetodo(faturacaoPorMetodo(inicio, fim));
        return resumo;
    }

    @Override
    public String gerarCsv(RelatorioFiltroFormDto filtro) {
        RelatorioResumoDto resumo = gerarRelatorio(filtro);
        return "periodo_start,periodo_end,ocupacaoPerc,estadiasCount,reservasCount,faturacaoTotal,pagamentosPendentes,servicosExtraTotal\n"
            + filtro.getDataInicio() + ","
            + filtro.getDataFim() + ","
            + String.format(java.util.Locale.US, "%.2f", resumo.getTaxaOcupacao()) + ","
            + resumo.getEstadiasCount() + ","
            + resumo.getReservasCount() + ","
            + resumo.getFaturacaoTotal() + ","
            + resumo.getPagamentosPendentes() + ","
            + resumo.getServicosExtraTotal() + "\n";
    }

    @Override
    public byte[] gerarPdf(RelatorioFiltroFormDto filtro) {
        RelatorioResumoDto resumo = gerarRelatorio(filtro);
        String conteudo = """
            Relatório operacional
            Período: %s a %s
            Taxa de ocupação: %.2f%%
            Estadias: %d
            Reservas: %d
            Faturação total: %s
            Pagamentos pendentes: %d
            Serviços extra: %s
            """.formatted(
                filtro.getDataInicio(),
                filtro.getDataFim(),
                resumo.getTaxaOcupacao(),
                resumo.getEstadiasCount(),
                resumo.getReservasCount(),
                resumo.getFaturacaoTotal(),
                resumo.getPagamentosPendentes(),
                resumo.getServicosExtraTotal()
            );
        return conteudo.getBytes(StandardCharsets.UTF_8);
    }

    private Map<String, BigDecimal> faturacaoPorMetodo(LocalDateTime inicio, LocalDateTime fim) {
        Map<String, BigDecimal> valores = new LinkedHashMap<>();
        for (MetodoPagamento metodo : MetodoPagamento.values()) {
            valores.put(metodo.name(), BigDecimal.ZERO);
        }
        for (Object[] row : pagamentoRepository.sumValorPorMetodo(inicio, fim)) {
            valores.put(String.valueOf(row[0]), (BigDecimal) row[1]);
        }
        return valores;
    }

    private void validarPeriodo(RelatorioFiltroFormDto filtro) {
        if (filtro.getDataInicio() == null || filtro.getDataFim() == null) {
            throw new IllegalArgumentException("Período obrigatório");
        }
        if (filtro.getDataInicio().isAfter(filtro.getDataFim())) {
            throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim");
        }
        if (filtro.getAgruparPor() == null) {
            filtro.setAgruparPor(RelatorioFiltroFormDto.GrupoRelatorio.MES);
        }
    }

    public RelatorioFiltroFormDto filtroMesAtual() {
        LocalDate hoje = LocalDate.now();
        RelatorioFiltroFormDto filtro = new RelatorioFiltroFormDto();
        filtro.setDataInicio(hoje.withDayOfMonth(1));
        filtro.setDataFim(hoje);
        return filtro;
    }
}
