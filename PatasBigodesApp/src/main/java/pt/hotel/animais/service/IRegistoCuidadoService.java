package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.hotel.animais.dto.RegistoCuidadoDto;
import pt.hotel.animais.dto.RegistoCuidadoFormDto;

public interface IRegistoCuidadoService {
    RegistoCuidadoDto create(RegistoCuidadoFormDto req, Long autorId);

    Page<RegistoCuidadoDto> listByEstadia(Long estadiaId, Pageable pageable);
}
