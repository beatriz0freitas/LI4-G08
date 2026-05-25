package pt.hotel.animais.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para plano de cuidados.
 * LAC-02: Retorna o plano com tarefas, prioridade e instruções
 */
public class PlanoCuidadosDto {
    private Long id;
    private Long animalId;
    private Long estadiaId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String prioridade;
    private Boolean ativo;
    private String instrucoes;
    private List<TarefaCuidadoDto> tarefas;

    public PlanoCuidadosDto() {}

    public PlanoCuidadosDto(Long id, Long animalId, Long estadiaId, LocalDateTime dataInicio,
                            LocalDateTime dataFim, String prioridade, Boolean ativo, String instrucoes) {
        this.id = id;
        this.animalId = animalId;
        this.estadiaId = estadiaId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.prioridade = prioridade;
        this.ativo = ativo;
        this.instrucoes = instrucoes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }
    public Long getEstadiaId() { return estadiaId; }
    public void setEstadiaId(Long estadiaId) { this.estadiaId = estadiaId; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public String getInstrucoes() { return instrucoes; }
    public void setInstrucoes(String instrucoes) { this.instrucoes = instrucoes; }
    public List<TarefaCuidadoDto> getTarefas() { return tarefas; }
    public void setTarefas(List<TarefaCuidadoDto> tarefas) { this.tarefas = tarefas; }
}
