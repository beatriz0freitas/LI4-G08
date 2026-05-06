package pt.hotel.animais.model.enums;

/**
 * Tipo funcional do alojamento, usado para garantir compatibilidade com a espécie do animal.
 */
public enum TipoAlojamento {
    CANINO("Canino"),
    FELINO("Felino");

    private final String label;

    TipoAlojamento(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TipoAlojamento fromEspecie(Especie especie) {
        if (especie == null) {
            throw new IllegalArgumentException("Espécie do animal é obrigatória");
        }
        return switch (especie) {
            case CAO -> CANINO;
            case GATO -> FELINO;
        };
    }
}
