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
import java.util.List;
import pt.hotel.animais.dto.HistoricoFiltroDto;
import pt.hotel.animais.dto.HistoricoItemDto;
import pt.hotel.animais.repository.RegistoCuidadoRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.repository.NotaRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class HistoricoService implements IHistoricoService {

    private final EstadiaRepository estadiaRepository;

    public HistoricoService(EstadiaRepository estadiaRepository) {
        this.estadiaRepository = estadiaRepository;
    }

    /**
     * Consulta consolidada do historial: agrega registos de cuidado, serviços extra, intervenções e notas.
     */
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<HistoricoItemDto> consultar(HistoricoFiltroDto filtro, org.springframework.data.domain.Pageable pageable,
                                                                              RegistoCuidadoRepository regRepo,
                                                                              ServicoExtraRepository seRepo,
                                                                              IntervencaoClinicaRepository icRepo,
                                                                              NotaRepository notaRepo) {
        // Only implement simple path when estadiaId provided
        Long estadiaId = filtro.getEstadiaId();
        var items = new ArrayList<HistoricoItemDto>();
        if (estadiaId != null) {
            var cuidados = regRepo.findByEstadiaIdOrderByDataHoraDesc(estadiaId);
            items.addAll(cuidados.stream().map(c -> {
                var h = new HistoricoItemDto();
                h.setTipo("REGISTO_CUIDADO");
                h.setId(c.getId());
                h.setEstadiaId(c.getEstadia() != null ? c.getEstadia().getId() : null);
                h.setDescricao(c.getDescricao());
                h.setDataHora(c.getDataHora());
                return h;
            }).collect(Collectors.toList()));

            var extras = seRepo.findByEstadiaId(estadiaId);
            items.addAll(extras.stream().map(e -> {
                var h = new HistoricoItemDto();
                h.setTipo("SERVICO_EXTRA");
                h.setId(e.getId());
                h.setEstadiaId(e.getEstadia() != null ? e.getEstadia().getId() : null);
                h.setDescricao(e.getTipo() + " - " + e.getCusto());
                h.setDataHora(e.getDataHora());
                return h;
            }).collect(Collectors.toList()));

            var interv = icRepo.findByEstadiaId(estadiaId);
            items.addAll(interv.stream().map(i -> {
                var h = new HistoricoItemDto();
                h.setTipo("INTERVENCAO_CLINICA");
                h.setId(i.getId());
                h.setEstadiaId(i.getEstadia() != null ? i.getEstadia().getId() : null);
                h.setDescricao(i.getDescricao());
                h.setDataHora(i.getDataHora());
                return h;
            }).collect(Collectors.toList()));

            var estadia = estadiaRepository.findById(estadiaId).orElse(null);
            if (estadia != null && estadia.getReserva() != null) {
                var notas = notaRepo.findByReservaId(estadia.getReserva().getId());
                items.addAll(notas.stream().map(n -> {
                    var h = new HistoricoItemDto();
                    h.setTipo("NOTA");
                    h.setId(n.getId());
                    h.setEstadiaId(estadiaId);
                    h.setDescricao(n.getDescricao());
                    h.setDataHora(n.getDataHora());
                    return h;
                }).collect(Collectors.toList()));
            }
        }

        // sort by dataHora desc
        items.sort(Comparator.comparing(HistoricoItemDto::getDataHora, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        // simple paging
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), items.size());
        List<HistoricoItemDto> pageContent = start > end ? List.of() : items.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, items.size());
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
