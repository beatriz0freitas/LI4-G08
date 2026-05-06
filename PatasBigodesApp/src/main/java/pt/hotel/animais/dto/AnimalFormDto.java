package pt.hotel.animais.dto;

import jakarta.validation.constraints.*;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoSaude;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para formulário de registo/edição de um animal.
 */
public class AnimalFormDto {
    
    private Long tutorId;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 150, message = "Nome deve ter entre 2 e 150 caracteres")
    private String nome;
    
    @NotNull(message = "Espécie é obrigatória")
    private Especie especie;
    
    @NotBlank(message = "Raça é obrigatória")
    @Size(min = 2, max = 100, message = "Raça deve ter entre 2 e 100 caracteres")
    private String raca;
    
    @NotNull(message = "Data de nascimento é obrigatória")
    @PastOrPresent(message = "Data de nascimento não pode ser no futuro")
    private LocalDate dataNascimento;
    
    @NotNull(message = "Peso é obrigatório")
    @Positive(message = "Peso deve ser positivo")
    @DecimalMin(value = "0.1", message = "Peso deve ser maior que 0")
    private BigDecimal peso;
    
    @NotNull(message = "Estado de saúde é obrigatório")
    private EstadoSaude estadoSaude = EstadoSaude.NORMAL;
    
    @Size(max = 500, message = "Necessidades alimentares não pode exceder 500 caracteres")
    private String necessidadesAlimentares;
    
    @Size(max = 500, message = "Medicação não pode exceder 500 caracteres")
    private String medicacaoCurso;
    
    // Getters e Setters
    
    public Long getTutorId() {
        return tutorId;
    }
    
    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Especie getEspecie() {
        return especie;
    }
    
    public void setEspecie(Especie especie) {
        this.especie = especie;
    }
    
    public String getRaca() {
        return raca;
    }
    
    public void setRaca(String raca) {
        this.raca = raca;
    }
    
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    public BigDecimal getPeso() {
        return peso;
    }
    
    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }
    
    public EstadoSaude getEstadoSaude() {
        return estadoSaude;
    }
    
    public void setEstadoSaude(EstadoSaude estadoSaude) {
        this.estadoSaude = estadoSaude;
    }
    
    public String getNecessidadesAlimentares() {
        return necessidadesAlimentares;
    }
    
    public void setNecessidadesAlimentares(String necessidadesAlimentares) {
        this.necessidadesAlimentares = necessidadesAlimentares;
    }
    
    public String getMedicacaoCurso() {
        return medicacaoCurso;
    }
    
    public void setMedicacaoCurso(String medicacaoCurso) {
        this.medicacaoCurso = medicacaoCurso;
    }
}
