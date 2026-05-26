package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registo de serviço extra fornecido durante uma estadia.
 * Referencia um tipo de serviço gerido pelo diretor (TipoServicoExtra).
 * Custos negativos são rejeitados na validação.
 */
@Entity
@Table(name = "servico_extra")
@Getter
@Setter
public class ServicoExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estadia_id", nullable = false)
    private Estadia estadia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_servico_extra_id", nullable = false)
    private TipoServicoExtra tipoServicoExtra;

    @Column(name = "custo", nullable = false, precision = 10, scale = 2)
    private BigDecimal custo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "autor_id")
    private Long autorId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        validarCusto();
    }

    @PreUpdate
    protected void onUpdate() {
        validarCusto();
    }

    private void validarCusto() {
        if (custo != null && custo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Custo do serviço extra não pode ser negativo");
        }
    }
}
