package pt.hotel.animais.dto;

import java.math.BigDecimal;

public class ReservaDetalheFinanceiroDto {
    private final Long estadiaId;
    private final BigDecimal extrasTotal;

    public ReservaDetalheFinanceiroDto(Long estadiaId, BigDecimal extrasTotal) {
        this.estadiaId = estadiaId;
        this.extrasTotal = extrasTotal;
    }

    public Long getEstadiaId() {
        return estadiaId;
    }

    public BigDecimal getExtrasTotal() {
        return extrasTotal;
    }
}
