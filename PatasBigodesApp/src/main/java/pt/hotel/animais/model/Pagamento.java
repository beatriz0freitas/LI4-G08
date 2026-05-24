package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import pt.hotel.animais.model.enums.EstadoPagamento;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.model.enums.MomentoPagamento;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
@Getter
@Setter
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estadia_id", nullable = false)
    private Estadia estadia;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", nullable = false, length = 50)
    private MetodoPagamento metodoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private MomentoPagamento momentoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoPagamento estadoPagamento = EstadoPagamento.LIQUIDADO;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
}
