package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Configuração de tarifas por tipo de alojamento.
 * Gerido pelo diretor através de operações CRUD.
 * Permite criar, editar, desativar tipos de alojamento e suas tarifas diárias.
 * 
 * Exemplo de negócio:
 * - Canino: 15.00€/dia (ativo)
 * - Felino: 10.00€/dia (ativo)
 * - Exótico: 25.00€/dia (pode ser adicionado e depois desativado)
 */
@Entity
@Table(name = "tipo_alojamento_tarifa", uniqueConstraints = {
    @UniqueConstraint(columnNames = "tipo_alojamento", name = "uk_tipo_alojamento")
})
@Getter
@Setter
public class TipoAlojamentoTarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_alojamento", nullable = false, unique = true, length = 50)
    private String tipoAlojamento;

    @Column(name = "tarifa_diaria", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaDiaria;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        validarTarifa();
    }

    @PreUpdate
    protected void onUpdate() {
        validarTarifa();
    }

    private void validarTarifa() {
        if (tarifaDiaria != null && tarifaDiaria.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tarifa diária não pode ser negativa");
        }
    }

    public TipoAlojamentoTarifa() {
    }

    public TipoAlojamentoTarifa(String tipoAlojamento, BigDecimal tarifaDiaria) {
        this.tipoAlojamento = tipoAlojamento;
        this.tarifaDiaria = tarifaDiaria;
        this.ativo = true;
    }
}
