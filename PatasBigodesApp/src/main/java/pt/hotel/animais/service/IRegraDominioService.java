package pt.hotel.animais.service;

import org.springframework.data.domain.Pageable;
import pt.hotel.animais.model.Estadia;

import java.time.LocalDate;

public interface IRegraDominioService {
    void validarPeriodo(LocalDate inicio, LocalDate fim);

    void validarEstadiaAtiva(Estadia estadia);

    Pageable normalizarPageable(Pageable pageable);
}
