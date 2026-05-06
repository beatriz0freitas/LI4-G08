package pt.hotel.animais.dto;

import java.time.LocalDate;

/**
 * DTO para representar a disponibilidade de um alojamento para um período.
 */
public class DisponibilidadeAlojamentoDto {
    
    private Long alojamentoId;
    private String identificacao;
    private String tipo;
    private Integer capacidade;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private boolean disponivel;
    private String motivoIndisponibilidade;
    
    // Constructores
    
    public DisponibilidadeAlojamentoDto() {}
    
    public DisponibilidadeAlojamentoDto(Long alojamentoId, String identificacao, String tipo, Integer capacidade) {
        this.alojamentoId = alojamentoId;
        this.identificacao = identificacao;
        this.tipo = tipo;
        this.capacidade = capacidade;
        this.disponivel = true;
    }
    
    // Getters e Setters
    
    public Long getAlojamentoId() {
        return alojamentoId;
    }
    
    public void setAlojamentoId(Long alojamentoId) {
        this.alojamentoId = alojamentoId;
    }
    
    public String getIdentificacao() {
        return identificacao;
    }
    
    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public Integer getCapacidade() {
        return capacidade;
    }
    
    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }
    
    public LocalDate getDataInicio() {
        return dataInicio;
    }
    
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }
    
    public LocalDate getDataFim() {
        return dataFim;
    }
    
    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
    
    public boolean isDisponivel() {
        return disponivel;
    }
    
    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
    
    public String getMotivoIndisponibilidade() {
        return motivoIndisponibilidade;
    }
    
    public void setMotivoIndisponibilidade(String motivoIndisponibilidade) {
        this.motivoIndisponibilidade = motivoIndisponibilidade;
    }
}
