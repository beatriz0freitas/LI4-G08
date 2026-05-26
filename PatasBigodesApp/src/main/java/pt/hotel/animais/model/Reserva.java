package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.hotel.animais.model.enums.EstadoReserva;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma reserva de alojamento para um animal.
 */
@Entity
@Table(name = "reserva")
@Getter
@Setter
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
    
    /**
     * Verifica se a reserva está ativa (pode receber operações).
     */
    public boolean isAtiva() {
        return estado == EstadoReserva.ATIVA;
    }

    public boolean isConfirmada() {
        return estado == EstadoReserva.CONFIRMADA;
    }

    public boolean podeFazerCheckIn() {
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
