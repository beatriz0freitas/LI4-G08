package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.hotel.animais.model.enums.PeriodicidadeTarefa;
import java.time.LocalDateTime;

/**
 * Tarefa de cuidado dentro de um plano.
 * Diferente de RegistoCuidado (anotações livres):
 * - TarefaCuidado: estruturado, com tipo e periodicidade, checklist
 * - RegistoCuidado: anotações livres sobre cuidados realizados
 */
@Entity
@Table(name = "tarefa_cuidado")
@Getter
@Setter
public class TarefaCuidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_cuidados_id", nullable = false)
    private PlanoCuidados planoCuidados;

    @Column(name = "tipo", nullable = false, length = 100)
    private String tipo;  // ALIMENTACAO_MANHA, MEDICACAO_12H, PASSEIO, LIMPEZA, OUTRO

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodicidade", nullable = false, length = 50)
    private PeriodicidadeTarefa periodicidade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "concluida", nullable = false)
    private Boolean concluida = false;

    @Column(name = "autor_conclusao_id")
    private Long autorConclusaoId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
