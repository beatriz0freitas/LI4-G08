package pt.hotel.animais.model.enums;

public enum TipoColaborador {
    DIRETOR("Diretor"),
    FUNCIONARIO_RECEPCAO("Funcionário Receção"),
    CUIDADOR("Cuidador"),
    MEDICO_VETERINARIO("Médico Veterinário"),
    RESPONSAVEL_LIMPEZA("Responsável Limpeza");

    private final String descricao;

    TipoColaborador(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
