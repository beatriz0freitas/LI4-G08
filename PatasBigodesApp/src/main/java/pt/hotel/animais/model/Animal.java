package pt.hotel.animais.model;

import jakarta.persistence.*;
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
    
    // Getters e Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Tutor getTutor() {
        return tutor;
    }
    
    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Especie getEspecie() {
        return especie;
    }
    
    public void setEspecie(Especie especie) {
        this.especie = especie;
    }
    
    public String getRaca() {
        return raca;
    }
    
    public void setRaca(String raca) {
        this.raca = raca;
    }
    
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    public BigDecimal getPeso() {
        return peso;
    }
    
    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }
    
    public EstadoSaude getEstadoSaude() {
        return estadoSaude;
    }
    
    public void setEstadoSaude(EstadoSaude estadoSaude) {
        this.estadoSaude = estadoSaude;
    }
    
    public String getNecessidadesAlimentares() {
        return necessidadesAlimentares;
    }
    
    public void setNecessidadesAlimentares(String necessidadesAlimentares) {
        this.necessidadesAlimentares = necessidadesAlimentares;
    }
    
    public String getMedicacaoCurso() {
        return medicacaoCurso;
    }
    
    public void setMedicacaoCurso(String medicacaoCurso) {
        this.medicacaoCurso = medicacaoCurso;
    }
    
    public LocalDateTime getDataRegisto() {
        return dataRegisto;
    }
    
    public void setDataRegisto(LocalDateTime dataRegisto) {
        this.dataRegisto = dataRegisto;
    }
    
    public List<Reserva> getReservas() {
        return reservas;
    }
    
    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
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
