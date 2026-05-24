package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeDto;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeFormDto;

public interface IAlteracaoEstadoSaudeService {
    AlteracaoEstadoSaudeDto register(AlteracaoEstadoSaudeFormDto form);

    Page<AlteracaoEstadoSaudeDto> listByEstadia(Long estadiaId, Pageable pageable);
}
