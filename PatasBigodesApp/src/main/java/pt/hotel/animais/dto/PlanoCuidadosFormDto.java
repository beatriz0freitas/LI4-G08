package pt.hotel.animais.dto;

/**
 * DTO de formulário para criar/editar plano de cuidados.
 * LAC-02: Suporta adição dinâmica de instruções durante a estadia
 */
public class PlanoCuidadosFormDto {
    private String instrucoes;

    public PlanoCuidadosFormDto() {}

    public PlanoCuidadosFormDto(String instrucoes) {
        this.instrucoes = instrucoes;
    }

    public String getInstrucoes() { return instrucoes; }
    public void setInstrucoes(String instrucoes) { this.instrucoes = instrucoes; }
}
