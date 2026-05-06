package pt.hotel.animais.dto;

import java.time.LocalDate;

/**
 * DTO para representar datas alternativas quando não há disponibilidade para o período solicitado.
 */
public class AlternativaDatasDto {
    
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int alojamentosDisponiveis;
    
    // Constructores
    
    public AlternativaDatasDto() {}
    
    public AlternativaDatasDto(LocalDate dataInicio, LocalDate dataFim, int alojamentosDisponiveis) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.alojamentosDisponiveis = alojamentosDisponiveis;
    }
    
    // Getters e Setters
    
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
    
    public int getAlojamentosDisponiveis() {
        return alojamentosDisponiveis;
    }
    
    public void setAlojamentosDisponiveis(int alojamentosDisponiveis) {
        this.alojamentosDisponiveis = alojamentosDisponiveis;
    }
}
