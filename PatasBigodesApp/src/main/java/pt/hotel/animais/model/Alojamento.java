package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.*;
import pt.hotel.animais.model.enums.EstadoLimpeza;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alojamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identificacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLimpeza estadoLimpeza;
}
