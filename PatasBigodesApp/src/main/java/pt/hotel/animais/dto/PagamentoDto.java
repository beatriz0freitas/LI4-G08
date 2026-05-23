package pt.hotel.animais.dto;

import java.math.BigDecimal;
import pt.hotel.animais.model.enums.MomentoPagamento;
import pt.hotel.animais.model.enums.EstadoPagamento;
import pt.hotel.animais.model.enums.MetodoPagamento;

public class PagamentoDto {
    private Long estadiaId;
    private BigDecimal valor;
    private MetodoPagamento metodoPagamento;
    private MomentoPagamento momentoPagamento;
    private EstadoPagamento estadoPagamento;

    public Long getEstadiaId() { return estadiaId; }
    public void setEstadiaId(Long estadiaId) { this.estadiaId = estadiaId; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    public MomentoPagamento getMomentoPagamento() { return momentoPagamento; }
    public void setMomentoPagamento(MomentoPagamento momentoPagamento) { this.momentoPagamento = momentoPagamento; }
    public EstadoPagamento getEstadoPagamento() { return estadoPagamento; }
    public void setEstadoPagamento(EstadoPagamento estadoPagamento) { this.estadoPagamento = estadoPagamento; }
}
