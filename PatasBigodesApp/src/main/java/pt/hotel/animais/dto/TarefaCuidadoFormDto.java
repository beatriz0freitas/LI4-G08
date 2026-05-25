package pt.hotel.animais.dto;

import java.time.LocalDateTime;

/**
 * DTO de formulário para criar/editar uma tarefa de cuidado.
 * LAC-02: Permite adicionar tarefas estruturadas durante a estadia
 */
public class TarefaCuidadoFormDto {
    private String tipo;
    private String descricao;
    private String periodicidade;
    private LocalDateTime dataHora;

    public TarefaCuidadoFormDto() {}

    public TarefaCuidadoFormDto(String tipo, String descricao, String periodicidade, LocalDateTime dataHora) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.periodicidade = periodicidade;
        this.dataHora = dataHora;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(String periodicidade) { this.periodicidade = periodicidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
