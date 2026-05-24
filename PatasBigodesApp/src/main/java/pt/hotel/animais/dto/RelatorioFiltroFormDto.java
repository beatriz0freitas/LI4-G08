package pt.hotel.animais.dto;

import jakarta.validation.constraints.NotNull;
import pt.hotel.animais.model.enums.TipoAlojamento;

import java.time.LocalDate;

public class RelatorioFiltroFormDto {

    @NotNull(message = "Data de início é obrigatória")
    private LocalDate dataInicio;

    @NotNull(message = "Data de fim é obrigatória")
    private LocalDate dataFim;

    private TipoAlojamento tipoAlojamento;
    private boolean incluirServicosExtra = true;
    private GrupoRelatorio agruparPor = GrupoRelatorio.MES;

    public enum GrupoRelatorio {
        DIA,
        SEMANA,
        MES
    }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public TipoAlojamento getTipoAlojamento() { return tipoAlojamento; }
    public void setTipoAlojamento(TipoAlojamento tipoAlojamento) { this.tipoAlojamento = tipoAlojamento; }
    public boolean isIncluirServicosExtra() { return incluirServicosExtra; }
    public void setIncluirServicosExtra(boolean incluirServicosExtra) { this.incluirServicosExtra = incluirServicosExtra; }
    public GrupoRelatorio getAgruparPor() { return agruparPor; }
    public void setAgruparPor(GrupoRelatorio agruparPor) { this.agruparPor = agruparPor; }
}
