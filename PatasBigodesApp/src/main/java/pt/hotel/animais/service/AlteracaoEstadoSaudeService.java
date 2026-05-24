package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeDto;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeFormDto;
import pt.hotel.animais.model.AlteracaoEstadoSaude;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.repository.AlteracaoEstadoSaudeRepository;
import pt.hotel.animais.repository.EstadiaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlteracaoEstadoSaudeService {

    private final AlteracaoEstadoSaudeRepository repository;
    private final EstadiaRepository estadiaRepository;

    @Transactional
    public AlteracaoEstadoSaudeDto register(AlteracaoEstadoSaudeFormDto form) {
        Estadia estadia = estadiaRepository.findById(form.getEstadiaId())
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        AlteracaoEstadoSaude a = new AlteracaoEstadoSaude();
        a.setEstadia(estadia);
        a.setDescricao(form.getDescricao());
        if (form.getSeveridade() != null) {
            try {
                a.setSeveridade(pt.hotel.animais.model.enums.EstadoSaude.valueOf(form.getSeveridade().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Severidade inválida: " + form.getSeveridade());
            }
        }
        a.setDataHora(form.getDataHora());

        AlteracaoEstadoSaude saved = repository.save(a);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<AlteracaoEstadoSaudeDto> listByEstadia(Long estadiaId, Pageable pageable) {
        List<AlteracaoEstadoSaude> list = repository.findByEstadiaIdOrderByDataHoraDesc(estadiaId);
        List<AlteracaoEstadoSaudeDto> dtos = list.stream().map(this::toDto).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        List<AlteracaoEstadoSaudeDto> pageContent = start > end ? List.of() : dtos.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    private AlteracaoEstadoSaudeDto toDto(AlteracaoEstadoSaude a) {
        AlteracaoEstadoSaudeDto d = new AlteracaoEstadoSaudeDto();
        d.setId(a.getId());
        d.setEstadiaId(a.getEstadia() != null ? a.getEstadia().getId() : null);
        d.setDescricao(a.getDescricao());
        d.setSeveridade(a.getSeveridade() != null ? a.getSeveridade().name() : null);
        d.setDataHora(a.getDataHora());
        return d;
    }
}
