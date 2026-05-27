package pt.hotel.animais.dto;

import java.math.BigDecimal;

/**
 * Linha agregada de relatório reutilizada por web, CSV e PDF.
 */
public class RelatorioAgrupamentoDto {

    private final String chave;
    private long reservas;
    private long estadias;
    private BigDecimal faturacaoTotal = BigDecimal.ZERO;
    private BigDecimal servicosExtraTotal = BigDecimal.ZERO;

    public RelatorioAgrupamentoDto(String chave) {
        this.chave = chave;
    }

    public String getChave() {
        return chave;
    }

    public long getReservas() {
        return reservas;
    }

    public long getEstadias() {
        return estadias;
    }

    public BigDecimal getFaturacaoTotal() {
        return faturacaoTotal;
    }

    public BigDecimal getServicosExtraTotal() {
        return servicosExtraTotal;
    }

    public void incrementarReservas() {
        reservas++;
    }

    public void incrementarEstadias() {
        estadias++;
    }

    public void adicionarFaturacao(BigDecimal valor) {
        if (valor != null) {
            faturacaoTotal = faturacaoTotal.add(valor);
        }
    }

    public void adicionarServicosExtra(BigDecimal valor) {
        if (valor != null) {
            servicosExtraTotal = servicosExtraTotal.add(valor);
        }
    }
}