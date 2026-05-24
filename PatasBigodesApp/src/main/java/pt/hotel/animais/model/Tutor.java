package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um tutor (responsável) de um animal.
 */
@Entity
@Table(name = "tutor")
@Getter
@Setter
public class Tutor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;
    
    @Column(name = "nif", nullable = false, unique = true, length = 20)
    private String nif;
    
    @Column(name = "contacto", nullable = false, length = 30)
    private String contacto;
    
    @Column(name = "email", nullable = false, length = 150)
    private String email;
    
    @Column(name = "data_registo", nullable = false, updatable = false)
    private LocalDateTime dataRegisto;
    
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Animal> animais = new ArrayList<>();
    
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        dataRegisto = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Tutor{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", nif='" + nif + '\'' +
                ", contacto='" + contacto + '\'' +
                ", email='" + email + '\'' +
                ", dataRegisto=" + dataRegisto +
                '}';
    }
}
