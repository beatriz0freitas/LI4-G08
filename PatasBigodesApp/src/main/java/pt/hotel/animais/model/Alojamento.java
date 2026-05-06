package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.*;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alojamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identificacao;
    
    @Column(length = 50)
    private String tipo;
    
    @Column
    private Integer capacidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLimpeza estadoLimpeza;
    
    @OneToMany(mappedBy = "alojamento", cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<>();
}
