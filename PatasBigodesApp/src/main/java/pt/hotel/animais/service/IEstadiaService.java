package pt.hotel.animais.service;

import pt.hotel.animais.dto.EstadiaListDto;
import pt.hotel.animais.dto.ResumoCheckInDto;
import pt.hotel.animais.dto.ResumoCheckOutDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.MetodoPagamento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IEstadiaService {
    Estadia abrirEstadiaPorReserva(Long reservaId, MetodoPagamento metodoPagamento);

    Estadia checkOut(Long estadiaId, MetodoPagamento metodoPagamento);

    Optional<ResumoCheckInDto> obterResumoCheckIn(Long reservaId);

    Optional<ResumoCheckOutDto> obterResumoCheckOut(Long estadiaId);

    long contarEstadiasEmCurso();

    /**
     * Lista estadias com filtros opcionais.
     * @param estado Filtro por estado (null para todas)
     * @param dataInicio Filtro por data de início (null para todas)
     * @param dataFim Filtro por data de fim (null para todas)
     * @return Lista de DTOs ordenada por data de início DESC
     */
    List<EstadiaListDto> listarComFiltros(EstadoEstadia estado, LocalDate dataInicio, LocalDate dataFim);
}
