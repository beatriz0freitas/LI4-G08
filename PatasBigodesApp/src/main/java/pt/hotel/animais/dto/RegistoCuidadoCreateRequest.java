package pt.hotel.animais.dto;

import java.time.LocalDateTime;

public class RegistoCuidadoCreateRequest {
    private Long estadiaId;
    private String descricao;
    private LocalDateTime dataHora;

    public Long getEstadiaId() { return estadiaId; }
    public void setEstadiaId(Long estadiaId) { this.estadiaId = estadiaId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
