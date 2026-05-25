package pt.hotel.animais.dto;

import pt.hotel.animais.model.enums.PeriodicidadeTarefa;

import java.time.LocalDateTime;

/**
 * DTO para uma tarefa de cuidado.
 */
public class TarefaCuidadoDto {
    private Long id;
    private Long planoCuidadosId;
    private String tipo;
    private String descricao;
    private PeriodicidadeTarefa periodicidade;
    private LocalDateTime dataHora;
    private Boolean concluida;
    private Long autorConclusaoId;

    public TarefaCuidadoDto() {}

    public TarefaCuidadoDto(Long id, String tipo, String descricao, PeriodicidadeTarefa periodicidade, 
                            LocalDateTime dataHora, Boolean concluida) {
        this.id = id;
        this.tipo = tipo;
        this.descricao = descricao;
        this.periodicidade = periodicidade;
        this.dataHora = dataHora;
        this.concluida = concluida;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlanoCuidadosId() { return planoCuidadosId; }
    public void setPlanoCuidadosId(Long planoCuidadosId) { this.planoCuidadosId = planoCuidadosId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public PeriodicidadeTarefa getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(PeriodicidadeTarefa periodicidade) { this.periodicidade = periodicidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public Boolean getConcluida() { return concluida; }
    public void setConcluida(Boolean concluida) { this.concluida = concluida; }
    public Long getAutorConclusaoId() { return autorConclusaoId; }
    public void setAutorConclusaoId(Long autorConclusaoId) { this.autorConclusaoId = autorConclusaoId; }
}
