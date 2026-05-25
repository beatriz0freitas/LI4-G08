package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.hotel.animais.model.enums.PrioridadePlano;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Plano de cuidados para um animal durante uma estadia.
 * - Animal: mantém histórico persistente de planos
 * - Estadia: cada estadia tem um plano ativo (UNIQUE constraint)
 */
@Entity
@Table(name = "plano_cuidados")
@Getter
@Setter
public class PlanoCuidados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estadia_id", nullable = false, unique = true)
    private Estadia estadia;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade", nullable = false, length = 50)
    private PrioridadePlano prioridade = PrioridadePlano.ROTINA;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "instrucoes", length = 2000)
    private String instrucoes;

    @OneToMany(mappedBy = "planoCuidados", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TarefaCuidado> tarefas = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dataInicio == null) {
            dataInicio = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
