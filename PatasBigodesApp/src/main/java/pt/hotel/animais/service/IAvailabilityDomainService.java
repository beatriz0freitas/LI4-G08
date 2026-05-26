package pt.hotel.animais.service;

import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.Especie;

import java.time.LocalDate;

public interface IAvailabilityDomainService {

    boolean estaDisponivel(Long alojamentoId, LocalDate dataInicio, LocalDate dataFim);

    boolean estaDisponivel(Long alojamentoId, LocalDate dataInicio, LocalDate dataFim, Especie especie);

    Alojamento validarDisponivelParaReservaComLock(
        Long alojamentoId,
        LocalDate dataInicio,
        LocalDate dataFim,
        Especie especie
    );
}