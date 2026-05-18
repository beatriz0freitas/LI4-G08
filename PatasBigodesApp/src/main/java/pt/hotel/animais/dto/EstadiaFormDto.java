package pt.hotel.animais.dto;

import java.time.LocalDateTime;

public class EstadiaFormDto {
    private Long reservaId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
}
