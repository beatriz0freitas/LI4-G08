package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.hotel.animais.model.enums.EstadoEstadia;
import java.time.LocalDateTime;

@Entity
@Table(name = "estadia")
@Getter
@Setter
public class Estadia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoEstadia estado = EstadoEstadia.EM_CURSO;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
}
