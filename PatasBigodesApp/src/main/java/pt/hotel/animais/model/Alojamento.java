package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.TipoAlojamento;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Alojamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identificacao;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoAlojamento tipo;
    
    @Column
    private Integer capacidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLimpeza estadoLimpeza;
    
    @OneToMany(mappedBy = "alojamento", cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<>();

    public Alojamento() {
    }

    public Alojamento(Long id, String identificacao, TipoAlojamento tipo, Integer capacidade, EstadoLimpeza estadoLimpeza, List<Reserva> reservas) {
        this.id = id;
        this.identificacao = identificacao;
        this.tipo = tipo;
        this.capacidade = capacidade;
        this.estadoLimpeza = estadoLimpeza;
        this.reservas = reservas != null ? reservas : new ArrayList<>();
    }
}
