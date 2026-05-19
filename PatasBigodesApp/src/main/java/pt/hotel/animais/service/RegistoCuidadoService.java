package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pt.hotel.animais.dto.RegistoCuidadoCreateRequest;
import pt.hotel.animais.dto.RegistoCuidadoView;

@Service
public class RegistoCuidadoService {

    public RegistoCuidadoView create(RegistoCuidadoCreateRequest req) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Page<RegistoCuidadoView> listByEstadia(Long estadiaId, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
