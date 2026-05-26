package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.RegistoCuidadoDto;
import pt.hotel.animais.dto.RegistoCuidadoFormDto;
import pt.hotel.animais.model.RegistoCuidado;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.repository.RegistoCuidadoRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistoCuidadoService implements IRegistoCuidadoService {

    private final RegistoCuidadoRepository registoCuidadoRepository;
    private final EstadiaRepository estadiaRepository;
    private final AuditoriaOperacaoService auditoriaOperacaoService;

    @Transactional
    public RegistoCuidadoDto create(RegistoCuidadoFormDto req, Long autorId) {
        Estadia estadia = estadiaRepository.findById(req.getEstadiaId())
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        if (estadia.getEstado() != EstadoEstadia.EM_CURSO) {
            throw new IllegalArgumentException("Não é possível registar cuidado para estadia que não está em curso");
        }

        if (req.getDescricao() == null || req.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Descrição do registo de cuidado é obrigatória");
        }

        RegistoCuidado rc = new RegistoCuidado();
        rc.setEstadia(estadia);
        rc.setDescricao(req.getDescricao());
        rc.setDataHora(req.getDataHora());
        rc.setAutorId(autorId);

        RegistoCuidado saved = registoCuidadoRepository.save(rc);
        auditoriaOperacaoService.registarSucesso(
            autorId,
            "CUIDADO_REGISTADO",
            "Cuidado",
            saved.getId(),
            "CREATE",
            detalhesCuidado(saved)
        );
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<RegistoCuidadoDto> listByEstadia(Long estadiaId, Pageable pageable) {
        List<RegistoCuidado> list = registoCuidadoRepository.findByEstadiaIdOrderByDataHoraDesc(estadiaId);
        List<RegistoCuidadoDto> dtos = list.stream().map(this::toDto).collect(Collectors.toList());
        // simple in-memory paging
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        List<RegistoCuidadoDto> pageContent = start > end ? List.of() : dtos.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    private RegistoCuidadoDto toDto(RegistoCuidado r) {
        RegistoCuidadoDto d = new RegistoCuidadoDto();
        d.setId(r.getId());
        d.setEstadiaId(r.getEstadia() != null ? r.getEstadia().getId() : null);
        d.setDescricao(r.getDescricao());
        d.setDataHora(r.getDataHora());
        d.setAutorNome(r.getAutorId() != null ? String.valueOf(r.getAutorId()) : null);
        return d;
    }

    private Map<String, Object> detalhesCuidado(RegistoCuidado registo) {
        Map<String, Object> detalhes = new LinkedHashMap<>();
        detalhes.put("estadiaId", registo.getEstadia() != null ? registo.getEstadia().getId() : null);
        detalhes.put("autorId", registo.getAutorId());
        detalhes.put("dataHora", registo.getDataHora() != null ? registo.getDataHora().toString() : null);
        detalhes.put("descricao", registo.getDescricao());
        return detalhes;
    }
}
