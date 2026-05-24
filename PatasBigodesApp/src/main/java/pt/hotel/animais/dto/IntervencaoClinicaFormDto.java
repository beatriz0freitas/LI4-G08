package pt.hotel.animais.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IntervencaoClinicaFormDto {
    private Long estadiaId;
    private String descricao;
    private BigDecimal custo;
    private LocalDateTime dataHora;

    public Long getEstadiaId() { return estadiaId; }
    public void setEstadiaId(Long estadiaId) { this.estadiaId = estadiaId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getCusto() { return custo; }
    public void setCusto(BigDecimal custo) { this.custo = custo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
