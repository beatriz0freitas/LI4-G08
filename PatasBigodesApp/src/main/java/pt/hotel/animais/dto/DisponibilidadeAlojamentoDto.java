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
    private String estado;
    private Long reservaId;
    private Long estadiaId;
    private String animalNome;
    private LocalDate dataReservaInicio;
    private LocalDate dataReservaFim;
    
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

    public String getTipoLabel() {
        return tipo != null ? tipo : "";
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoLabel() {
        if (estado == null) {
            return "";
        }
        return switch (estado) {
            case "LIVRE" -> "Livre";
            case "OCUPADO" -> "Ocupado";
            case "RESERVADO" -> "Reservado";
            case "LIMPEZA" -> "Em Limpeza";
            default -> estado;
        };
    }

    public String getEstadoCss() {
        return estado != null ? "st-" + estado.toLowerCase() : "";
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public Long getEstadiaId() {
        return estadiaId;
    }

    public void setEstadiaId(Long estadiaId) {
        this.estadiaId = estadiaId;
    }

    public String getAnimalNome() {
        return animalNome;
    }

    public void setAnimalNome(String animalNome) {
        this.animalNome = animalNome;
    }

    public LocalDate getDataReservaInicio() {
        return dataReservaInicio;
    }

    public void setDataReservaInicio(LocalDate dataReservaInicio) {
        this.dataReservaInicio = dataReservaInicio;
    }

    public LocalDate getDataReservaFim() {
        return dataReservaFim;
    }

    public void setDataReservaFim(LocalDate dataReservaFim) {
        this.dataReservaFim = dataReservaFim;
    }
}
