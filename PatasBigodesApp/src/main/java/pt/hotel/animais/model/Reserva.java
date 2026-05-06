package pt.hotel.animais.model;

import jakarta.persistence.*;
import pt.hotel.animais.model.enums.EstadoReserva;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma reserva de alojamento para um animal.
 */
@Entity
@Table(name = "reserva")
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alojamento_id", nullable = false)
    private Alojamento alojamento;
    
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;
    
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.ATIVA;
    
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
    
    // Getters e Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Tutor getTutor() {
        return tutor;
    }
    
    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }
    
    public Animal getAnimal() {
        return animal;
    }
    
    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
    
    public Alojamento getAlojamento() {
        return alojamento;
    }
    
    public void setAlojamento(Alojamento alojamento) {
        this.alojamento = alojamento;
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
    
    public EstadoReserva getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    /**
     * Verifica se a reserva está ativa (pode receber operações).
     */
    public boolean isAtiva() {
        return estado == EstadoReserva.ATIVA;
    }
    
    /**
     * Verifica se a reserva pode ser cancelada.
     */
    public boolean podeSerCancelada() {
        return estado == EstadoReserva.ATIVA;
    }
    
    /**
     * Verifica se a data se sobrepõe com outra reserva.
     */
    public boolean temSobreposicao(LocalDate outraDataInicio, LocalDate outraDataFim) {
        return !(dataFim.isBefore(outraDataInicio) || dataInicio.isAfter(outraDataFim));
    }
    
    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", estado=" + estado +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}
