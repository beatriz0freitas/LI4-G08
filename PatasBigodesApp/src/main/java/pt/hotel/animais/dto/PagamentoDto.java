package pt.hotel.animais.dto;

import java.math.BigDecimal;
import pt.hotel.animais.model.enums.TipoPagamento;

public class PagamentoDto {
    private Long estadiaId;
    private BigDecimal valor;
    private String metodo;
    private TipoPagamento tipo;

    public Long getEstadiaId() { return estadiaId; }
    public void setEstadiaId(Long estadiaId) { this.estadiaId = estadiaId; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public TipoPagamento getTipo() { return tipo; }
    public void setTipo(TipoPagamento tipo) { this.tipo = tipo; }
}
