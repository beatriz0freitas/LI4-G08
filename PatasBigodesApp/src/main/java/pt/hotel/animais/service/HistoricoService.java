package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.repository.EstadiaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class HistoricoService {

    private final EstadiaRepository estadiaRepository;

    public HistoricoService(EstadiaRepository estadiaRepository) {
        this.estadiaRepository = estadiaRepository;
    }

    @Transactional(readOnly = true)
    public Page<Estadia> listarHistorico(
        Long clienteId,
        Long animalId,
        EstadoEstadia estado,
        LocalDate dataInicio,
        LocalDate dataFim,
        Pageable pageable
    ) {
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.plusDays(1).atStartOfDay().minusNanos(1) : null;

        return estadiaRepository.pesquisarHistorico(clienteId, animalId, estado, inicio, fim, pageable);
    }
}
