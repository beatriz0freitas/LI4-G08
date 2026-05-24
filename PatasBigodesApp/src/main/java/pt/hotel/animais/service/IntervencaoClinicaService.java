package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.IntervencaoClinicaDto;
import pt.hotel.animais.dto.IntervencaoClinicaFormDto;
import pt.hotel.animais.model.IntervencaoClinica;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.repository.EstadiaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntervencaoClinicaService implements IIntervencaoClinicaService {

    private final IntervencaoClinicaRepository intervencaoClinicaRepository;
    private final EstadiaRepository estadiaRepository;

    @Transactional
    public IntervencaoClinicaDto register(IntervencaoClinicaFormDto form) {
        Estadia estadia = estadiaRepository.findById(form.getEstadiaId())
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        IntervencaoClinica ic = new IntervencaoClinica();
        ic.setEstadia(estadia);
        ic.setDescricao(form.getDescricao());
        ic.setCusto(form.getCusto());
        ic.setDataHora(form.getDataHora());
        // medicoId/autor should be set from security context in controller

        IntervencaoClinica saved = intervencaoClinicaRepository.save(ic);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<IntervencaoClinicaDto> listByEstadia(Long estadiaId, Pageable pageable) {
        List<IntervencaoClinica> list = intervencaoClinicaRepository.findByEstadiaId(estadiaId);
        List<IntervencaoClinicaDto> dtos = list.stream().map(this::toDto).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        List<IntervencaoClinicaDto> pageContent = start > end ? List.of() : dtos.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    private IntervencaoClinicaDto toDto(IntervencaoClinica i) {
        IntervencaoClinicaDto d = new IntervencaoClinicaDto();
        d.setId(i.getId());
        d.setEstadiaId(i.getEstadia() != null ? i.getEstadia().getId() : null);
        d.setDescricao(i.getDescricao());
        d.setCusto(i.getCusto());
        d.setDataHora(i.getDataHora());
        return d;
    }
}
