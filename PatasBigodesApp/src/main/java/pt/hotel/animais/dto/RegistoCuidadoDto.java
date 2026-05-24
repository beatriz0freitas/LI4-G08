package pt.hotel.animais.dto;

import java.time.LocalDateTime;

public class RegistoCuidadoDto {
    private Long id;
    private Long estadiaId;
    private String descricao;
    private LocalDateTime dataHora;
    private String autorNome;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEstadiaId() { return estadiaId; }
    public void setEstadiaId(Long estadiaId) { this.estadiaId = estadiaId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getAutorNome() { return autorNome; }
    public void setAutorNome(String autorNome) { this.autorNome = autorNome; }
}
