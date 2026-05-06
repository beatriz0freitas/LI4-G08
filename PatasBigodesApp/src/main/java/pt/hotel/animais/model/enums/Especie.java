package pt.hotel.animais.model.enums;

/**
 * Enum para representar as espécies de animais suportadas pelo hotel.
 */
public enum Especie {
    CAO("Cão"),
    GATO("Gato");

    private final String label;

    Especie(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
