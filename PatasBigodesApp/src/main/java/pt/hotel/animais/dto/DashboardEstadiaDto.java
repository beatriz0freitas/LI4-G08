package pt.hotel.animais.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Linha resumida para a tabela de estadias em curso do dashboard de direção.
 */
public class DashboardEstadiaDto {
    private Long id;
    private String animal;
    private String especie;
    private String alojamento;
    private LocalDateTime dataEntrada;
    private LocalDate dataSaidaPrevista;
    private String estado;
    private boolean checkoutHoje;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAnimal() { return animal; }
    public void setAnimal(String animal) { this.animal = animal; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getAlojamento() { return alojamento; }
    public void setAlojamento(String alojamento) { this.alojamento = alojamento; }
    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }
    public LocalDate getDataSaidaPrevista() { return dataSaidaPrevista; }
    public void setDataSaidaPrevista(LocalDate dataSaidaPrevista) { this.dataSaidaPrevista = dataSaidaPrevista; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean isCheckoutHoje() { return checkoutHoje; }
    public void setCheckoutHoje(boolean checkoutHoje) { this.checkoutHoje = checkoutHoje; }
}
