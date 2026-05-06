package pt.hotel.animais.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um tutor (responsável) de um animal.
 */
@Entity
@Table(name = "tutor")
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
    
    // Getters e Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getNif() {
        return nif;
    }
    
    public void setNif(String nif) {
        this.nif = nif;
    }
    
    public String getContacto() {
        return contacto;
    }
    
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getDataRegisto() {
        return dataRegisto;
    }
    
    public void setDataRegisto(LocalDateTime dataRegisto) {
        this.dataRegisto = dataRegisto;
    }
    
    public List<Animal> getAnimais() {
        return animais;
    }
    
    public void setAnimais(List<Animal> animais) {
        this.animais = animais;
    }
    
    public List<Reserva> getReservas() {
        return reservas;
    }
    
    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
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
