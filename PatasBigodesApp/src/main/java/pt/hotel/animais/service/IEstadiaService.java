package pt.hotel.animais.service;

import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.MetodoPagamento;

public interface IEstadiaService {
    Estadia abrirEstadiaPorReserva(Long reservaId, MetodoPagamento metodoPagamento);

    Estadia checkOut(Long estadiaId, MetodoPagamento metodoPagamento);

    long contarEstadiasEmCurso();
}
