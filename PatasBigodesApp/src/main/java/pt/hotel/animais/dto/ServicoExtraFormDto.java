package pt.hotel.animais.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServicoExtraFormDto {
    private Long estadiaId;
    private String tipo;
    private BigDecimal custo;
    private LocalDateTime dataHora;

    public Long getEstadiaId() { return estadiaId; }
    public void setEstadiaId(Long estadiaId) { this.estadiaId = estadiaId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getCusto() { return custo; }
    public void setCusto(BigDecimal custo) { this.custo = custo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
