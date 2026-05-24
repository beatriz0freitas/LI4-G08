package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pt.hotel.animais.dto.ServicoExtraFormDto;
import pt.hotel.animais.dto.ServicoExtraDto;

@Service
public class ServicoExtraService {

    public ServicoExtraDto register(ServicoExtraFormDto req) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Page<ServicoExtraDto> listByEstadia(Long estadiaId, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
