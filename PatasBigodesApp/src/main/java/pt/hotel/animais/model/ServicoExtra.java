package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "custo", nullable = false)
    private BigDecimal custo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "autor_id")
    private Long autorId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
