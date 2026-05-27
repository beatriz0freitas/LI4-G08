package pt.hotel.animais.service;

import pt.hotel.animais.dto.ReservaDetalheFinanceiroDto;
import pt.hotel.animais.dto.ReservaFormDto;
import pt.hotel.animais.dto.ReservaListDto;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.EstadoReserva;

import java.util.List;
import java.util.Optional;

public interface IReservaService {
    Reserva criar(ReservaFormDto formDto);

    Reserva obter(Long id);

    Optional<ReservaDetalheFinanceiroDto> obterDetalheFinanceiro(Long id);

    List<Reserva> procurarPorTutor(Long tutorId);

    List<Reserva> procurarPorAnimal(Long animalId);

    List<Reserva> procurarAtivas(Long tutorId);

    List<Reserva> procurarPorAlojamento(Long alojamentoId);

    Reserva cancelar(Long id);

    Reserva confirmar(Long id);

    Reserva concluir(Long id);

    long contarReservasAtivas();

    long contarReservasFuturas();

    List<Reserva> listarTodas();

    /**
     * Lista reservas com filtro opcional por estado.
     * @param estado Filtro por estado (null para todas)
     * @return Lista de DTOs para exibição
     */
    List<ReservaListDto> listarComFiltros(EstadoReserva estado);
}
