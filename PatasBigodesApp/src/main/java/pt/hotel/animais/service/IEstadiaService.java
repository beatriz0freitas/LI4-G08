package pt.hotel.animais.service;

import pt.hotel.animais.dto.ResumoCheckInDto;
import pt.hotel.animais.dto.ResumoCheckOutDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.MetodoPagamento;

import java.util.Optional;

public interface IEstadiaService {
    Estadia abrirEstadiaPorReserva(Long reservaId, MetodoPagamento metodoPagamento);

    Estadia checkOut(Long estadiaId, MetodoPagamento metodoPagamento);

    Optional<ResumoCheckInDto> obterResumoCheckIn(Long reservaId);

    Optional<ResumoCheckOutDto> obterResumoCheckOut(Long estadiaId);

    long contarEstadiasEmCurso();
}
