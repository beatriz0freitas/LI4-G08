package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.hotel.animais.dto.HistoricoFiltroDto;
import pt.hotel.animais.dto.HistoricoItemDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;

import java.time.LocalDate;

public interface IHistoricoService {
    Page<HistoricoItemDto> consultar(
        HistoricoFiltroDto filtro,
        Pageable pageable
    );

    Page<Estadia> listarHistorico(
        Long clienteId,
        Long animalId,
        EstadoEstadia estado,
        LocalDate dataInicio,
        LocalDate dataFim,
        Pageable pageable
    );
}
