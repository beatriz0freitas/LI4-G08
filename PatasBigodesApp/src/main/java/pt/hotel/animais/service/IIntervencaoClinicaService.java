package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.hotel.animais.dto.IntervencaoClinicaDto;
import pt.hotel.animais.dto.IntervencaoClinicaFormDto;

public interface IIntervencaoClinicaService {
    IntervencaoClinicaDto register(IntervencaoClinicaFormDto form, Long autorId);

    Page<IntervencaoClinicaDto> listByEstadia(Long estadiaId, Pageable pageable);
}
