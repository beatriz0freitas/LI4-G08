package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoSaude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Entidade que representa um animal hospedado no hotel.
 */
@Entity
@Table(name = "animal")
@Getter
@Setter
public class Animal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;
    
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false, length = 20)
    private Especie especie;
    
    @Column(name = "raca", nullable = false, length = 100)
    private String raca;
    
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;
    
    @Column(name = "peso", nullable = false, precision = 6, scale = 2)
    private BigDecimal peso;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_saude", nullable = false, length = 20)
    private EstadoSaude estadoSaude = EstadoSaude.NORMAL;
    
    @Column(name = "necessidades_alimentares", columnDefinition = "TEXT")
    private String necessidadesAlimentares;
    
    @Column(name = "medicacao_curso", columnDefinition = "TEXT")
    private String medicacaoCurso;
    
    @Column(name = "data_registo", nullable = false, updatable = false)
    private LocalDateTime dataRegisto;
    
    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        dataRegisto = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especie=" + especie +
                ", raca='" + raca + '\'' +
                ", peso=" + peso +
                ", estadoSaude=" + estadoSaude +
                ", dataRegisto=" + dataRegisto +
                '}';
    }
}
