package pt.hotel.animais.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO de leitura com as métricas agregadas apresentadas no relatório.
 *
 * Não representa uma entidade persistida; é uma projeção calculada a partir de
 * reservas, estadias, pagamentos, alojamentos e serviços extra.
 */
public class RelatorioResumoDto {
    private long alojamentosTotal;
    private long alojamentosOcupados;
    private double taxaOcupacao;
    private long estadiasCount;
    private long reservasCount;
    private BigDecimal faturacaoTotal = BigDecimal.ZERO;
    private long pagamentosPendentes;
    private BigDecimal servicosExtraTotal = BigDecimal.ZERO;
    private Map<String, BigDecimal> faturacaoPorMetodo = new LinkedHashMap<>();
    private List<RelatorioAgrupamentoDto> agrupamentos = new ArrayList<>();

    public long getAlojamentosTotal() { return alojamentosTotal; }
    public void setAlojamentosTotal(long alojamentosTotal) { this.alojamentosTotal = alojamentosTotal; }
    public long getAlojamentosOcupados() { return alojamentosOcupados; }
    public void setAlojamentosOcupados(long alojamentosOcupados) { this.alojamentosOcupados = alojamentosOcupados; }
    public double getTaxaOcupacao() { return taxaOcupacao; }
    public void setTaxaOcupacao(double taxaOcupacao) { this.taxaOcupacao = taxaOcupacao; }
    public long getEstadiasCount() { return estadiasCount; }
    public void setEstadiasCount(long estadiasCount) { this.estadiasCount = estadiasCount; }
    public long getReservasCount() { return reservasCount; }
    public void setReservasCount(long reservasCount) { this.reservasCount = reservasCount; }
    public BigDecimal getFaturacaoTotal() { return faturacaoTotal; }
    public void setFaturacaoTotal(BigDecimal faturacaoTotal) { this.faturacaoTotal = faturacaoTotal; }
    public long getPagamentosPendentes() { return pagamentosPendentes; }
    public void setPagamentosPendentes(long pagamentosPendentes) { this.pagamentosPendentes = pagamentosPendentes; }
    public BigDecimal getServicosExtraTotal() { return servicosExtraTotal; }
    public void setServicosExtraTotal(BigDecimal servicosExtraTotal) { this.servicosExtraTotal = servicosExtraTotal; }
    public Map<String, BigDecimal> getFaturacaoPorMetodo() { return faturacaoPorMetodo; }
    public void setFaturacaoPorMetodo(Map<String, BigDecimal> faturacaoPorMetodo) { this.faturacaoPorMetodo = faturacaoPorMetodo; }
    public List<RelatorioAgrupamentoDto> getAgrupamentos() { return agrupamentos; }
    public void setAgrupamentos(List<RelatorioAgrupamentoDto> agrupamentos) { this.agrupamentos = agrupamentos; }
}
