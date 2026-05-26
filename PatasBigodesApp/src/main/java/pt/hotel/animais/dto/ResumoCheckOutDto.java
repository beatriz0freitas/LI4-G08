package pt.hotel.animais.dto;

import pt.hotel.animais.model.Estadia;

import java.math.BigDecimal;

public class ResumoCheckOutDto {
    private final Estadia estadia;
    private final BigDecimal valorCheckOut;

    public ResumoCheckOutDto(Estadia estadia, BigDecimal valorCheckOut) {
        this.estadia = estadia;
        this.valorCheckOut = valorCheckOut;
    }

    public Estadia getEstadia() {
        return estadia;
    }

    public BigDecimal getValorCheckOut() {
        return valorCheckOut;
    }
}
