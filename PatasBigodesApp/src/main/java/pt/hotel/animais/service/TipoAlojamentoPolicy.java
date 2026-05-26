package pt.hotel.animais.service;

import pt.hotel.animais.model.enums.Especie;

public final class TipoAlojamentoPolicy {

    private TipoAlojamentoPolicy() {
    }

    public static String fromEspecie(Especie especie) {
        if (especie == null) {
            throw new IllegalArgumentException("Espécie é obrigatória para determinar o tipo de alojamento");
        }
        return switch (especie) {
            case CAO -> "CANINO";
            case GATO -> "FELINO";
        };
    }
}
