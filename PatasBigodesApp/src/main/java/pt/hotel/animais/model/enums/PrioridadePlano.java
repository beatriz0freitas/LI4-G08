package pt.hotel.animais.model.enums;

/**
 * Prioridade do plano de cuidados.
 * LAC-02: Dinâmica conforme AlteracaoEstadoSaude (US-16)
 */
public enum PrioridadePlano {
    ROTINA,      // Cuidados normais, sem urgência
    URGENTE,     // Cuidados que requerem atenção reforçada
    CRITICO      // Cuidados imediatos, estado de saúde crítico
}
