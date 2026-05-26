package pt.hotel.animais.model.enums;

/**
 * Enum para representar os estados possíveis de uma reserva.
 */
public enum EstadoReserva {
    ATIVA("Ativa"),
    CONFIRMADA("Confirmada"),
    CANCELADA("Cancelada"),
    CONCLUIDA("Concluída");

    private final String label;

    EstadoReserva(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
