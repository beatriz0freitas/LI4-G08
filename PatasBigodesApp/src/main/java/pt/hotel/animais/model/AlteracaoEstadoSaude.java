package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.hotel.animais.model.enums.EstadoSaude;
import java.time.LocalDateTime;

@Entity
@Table(name = "alteracao_estado_saude")
@Getter
@Setter
public class AlteracaoEstadoSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estadia_id", nullable = false)
    private Estadia estadia;

    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "severidade", nullable = false, length = 50)
    private EstadoSaude severidade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "autor_id")
    private Long autorId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
