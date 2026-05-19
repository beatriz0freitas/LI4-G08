package pt.hotel.animais.model;

import jakarta.persistence.*;
import pt.hotel.animais.model.enums.EstadoSaude;
import java.time.LocalDateTime;

@Entity
@Table(name = "alteracao_estado_saude")
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Estadia getEstadia() { return estadia; }
    public void setEstadia(Estadia estadia) { this.estadia = estadia; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public EstadoSaude getSeveridade() { return severidade; }
    public void setSeveridade(EstadoSaude severidade) { this.severidade = severidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public Long getAutorId() { return autorId; }
    public void setAutorId(Long autorId) { this.autorId = autorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
