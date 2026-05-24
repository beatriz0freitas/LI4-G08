package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "intervencao_clinica")
@Getter
@Setter
public class IntervencaoClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estadia_id", nullable = false)
    private Estadia estadia;

    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;

    @Column(name = "custo", nullable = false)
    private BigDecimal custo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "medico_id")
    private Long medicoId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
