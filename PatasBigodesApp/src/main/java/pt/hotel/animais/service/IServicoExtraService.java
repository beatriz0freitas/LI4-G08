package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.hotel.animais.dto.ServicoExtraDto;
import pt.hotel.animais.dto.ServicoExtraFormDto;

public interface IServicoExtraService {
    ServicoExtraDto register(ServicoExtraFormDto req, Long autorId);

    Page<ServicoExtraDto> listByEstadia(Long estadiaId, Pageable pageable);
}
