package pt.hotel.animais.dto;

import java.time.LocalDateTime;

public class NotaFormDto {
    private Long reservaId;
    private String descricao;
    private LocalDateTime dataHora;

    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
