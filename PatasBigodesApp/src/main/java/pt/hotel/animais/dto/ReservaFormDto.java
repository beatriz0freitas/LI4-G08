package pt.hotel.animais.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO para formulário de criação de uma reserva.
 */
public class ReservaFormDto {
    
    @NotNull(message = "Tutor é obrigatório")
    private Long tutorId;
    
    @NotNull(message = "Animal é obrigatório")
    private Long animalId;
    
    @NotNull(message = "Alojamento é obrigatório")
    private Long alojamentoId;
    
    @NotNull(message = "Data de início é obrigatória")
    @FutureOrPresent(message = "Data de início não pode ser no passado")
    private LocalDate dataInicio;
    
    @NotNull(message = "Data de término é obrigatória")
    private LocalDate dataFim;
    
    // Validação customizada será feita no service
    // para garantir que dataFim > dataInicio e verificar disponibilidade
    
    // Getters e Setters
    
    public Long getTutorId() {
        return tutorId;
    }
    
    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }
    
    public Long getAnimalId() {
        return animalId;
    }
    
    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }
    
    public Long getAlojamentoId() {
        return alojamentoId;
    }
    
    public void setAlojamentoId(Long alojamentoId) {
        this.alojamentoId = alojamentoId;
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
}
