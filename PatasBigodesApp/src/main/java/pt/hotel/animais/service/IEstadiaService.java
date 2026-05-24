package pt.hotel.animais.service;

import pt.hotel.animais.model.Estadia;

public interface IEstadiaService {
    Estadia abrirEstadiaPorReserva(Long reservaId);

    Estadia checkOut(Long estadiaId);

    long contarEstadiasEmCurso();
}
