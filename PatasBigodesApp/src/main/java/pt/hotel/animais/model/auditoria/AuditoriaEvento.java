package pt.hotel.animais.model.auditoria;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.Getter;
import lombok.Setter;
import pt.hotel.animais.config.converter.AuditoriaDetalhesConverter;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.enums.ResultadoAuditoria;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Evento persistido de auditoria para operações críticas.
 */
@Entity
@Table(name = "auditoria_evento", indexes = {
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_utilizador_timestamp", columnList = "utilizador_id, timestamp"),
    @Index(name = "idx_operacao_timestamp", columnList = "operacao, timestamp"),
    @Index(name = "idx_entidade_id_timestamp", columnList = "entidade, entity_id, timestamp")
})
@Getter
@Setter
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilizador_id", nullable = false)
    private Colaborador utilizador;

    @Column(name = "operacao", nullable = false, length = 100)
    private String operacao;

    @Column(name = "entidade", nullable = false, length = 100)
    private String entidade;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "acao", nullable = false, length = 50)
    private String acao;

    @Convert(converter = AuditoriaDetalhesConverter.class)
    @Column(name = "detalhes", columnDefinition = "TEXT")
    private Map<String, Object> detalhes = new LinkedHashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, length = 20)
    private ResultadoAuditoria resultado;

    @Column(name = "motivo_falha", length = 500)
    private String motivoFalha;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (detalhes == null) {
            detalhes = new LinkedHashMap<>();
        }
    }
}
