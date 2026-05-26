package pt.hotel.animais.dto;

import pt.hotel.animais.model.Reserva;

import java.math.BigDecimal;

public class ResumoCheckInDto {
    private final Reserva reserva;
    private final BigDecimal valorCheckIn;

    public ResumoCheckInDto(Reserva reserva, BigDecimal valorCheckIn) {
        this.reserva = reserva;
        this.valorCheckIn = valorCheckIn;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public BigDecimal getValorCheckIn() {
        return valorCheckIn;
    }
}
