package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.NotaDto;
import pt.hotel.animais.dto.NotaFormDto;
import pt.hotel.animais.model.Nota;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.repository.NotaRepository;
import pt.hotel.animais.repository.ReservaRepository;

@Service
@RequiredArgsConstructor
public class NotaService implements INotaService {

    private final NotaRepository notaRepository;
    private final ReservaRepository reservaRepository;

    @Transactional
    public NotaDto create(NotaFormDto form, Long autorId) {
        Reserva reserva = reservaRepository.findById(form.getReservaId())
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));

        Nota n = new Nota();
        n.setReserva(reserva);
        n.setDescricao(form.getDescricao());
        n.setDataHora(form.getDataHora());
        n.setAutorId(autorId);

        Nota saved = notaRepository.save(n);
        NotaDto dto = new NotaDto();
        dto.setId(saved.getId());
        dto.setReservaId(reserva.getId());
        dto.setDescricao(saved.getDescricao());
        dto.setDataHora(saved.getDataHora());
        return dto;
    }
}
