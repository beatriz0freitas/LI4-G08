package pt.hotel.animais.dto;

import java.time.LocalDateTime;

public class HistoricoItemDto {
    private String tipo;
    private Long id;
    private Long estadiaId;
    private String descricao;
    private LocalDateTime dataHora;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEstadiaId() { return estadiaId; }
    public void setEstadiaId(Long estadiaId) { this.estadiaId = estadiaId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
