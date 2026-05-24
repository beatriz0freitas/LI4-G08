package pt.hotel.animais.service;

import pt.hotel.animais.dto.ReservaFormDto;
import pt.hotel.animais.model.Reserva;

import java.util.List;

public interface IReservaService {
    Reserva criar(ReservaFormDto formDto);

    Reserva obter(Long id);

    List<Reserva> procurarPorTutor(Long tutorId);

    List<Reserva> procurarPorAnimal(Long animalId);

    List<Reserva> procurarAtivas(Long tutorId);

    List<Reserva> procurarPorAlojamento(Long alojamentoId);

    Reserva cancelar(Long id);

    Reserva concluir(Long id);

    long contarReservasAtivas();

    long contarReservasFuturas();

    List<Reserva> listarTodas();
}
