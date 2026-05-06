package pt.hotel.animais.model.enums;

/**
 * Enum para representar o estado de saúde do animal durante o alojamento.
 */
public enum EstadoSaude {
    NORMAL("Normal"),
    ALTERADO("Alterado"),
    CRITICO("Crítico");

    private final String label;

    EstadoSaude(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
