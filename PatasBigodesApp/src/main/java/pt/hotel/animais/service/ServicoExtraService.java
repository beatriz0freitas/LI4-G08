package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.ServicoExtraFormDto;
import pt.hotel.animais.dto.ServicoExtraDto;
import pt.hotel.animais.model.ServicoExtra;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.repository.ServicoExtraRepository;
import pt.hotel.animais.repository.EstadiaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicoExtraService {

    private final ServicoExtraRepository servicoExtraRepository;
    private final EstadiaRepository estadiaRepository;
    private final PagamentoService pagamentoService;

    public ServicoExtraDto register(ServicoExtraFormDto req, Long autorId) {
        Estadia estadia = estadiaRepository.findById(req.getEstadiaId())
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        if (estadia.getEstado() != pt.hotel.animais.model.enums.EstadoEstadia.EM_CURSO) {
            throw new IllegalArgumentException("Só é possível registar serviços extra para estadias em curso");
        }

        ServicoExtra se = new ServicoExtra();
        se.setEstadia(estadia);
        se.setTipo(req.getTipo());
        se.setCusto(req.getCusto());
        se.setDataHora(req.getDataHora());
        se.setAutorId(autorId);

        ServicoExtra saved = servicoExtraRepository.save(se);

        // atualizar cálculo de extras (PagamentoService irá agregar na cobrança)
        try {
            pagamentoService.calcularExtras(estadia);
        } catch (Exception ignored) { }

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ServicoExtraDto> listByEstadia(Long estadiaId, Pageable pageable) {
        List<ServicoExtra> list = servicoExtraRepository.findByEstadiaId(estadiaId);
        List<ServicoExtraDto> dtos = list.stream().map(this::toDto).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        List<ServicoExtraDto> pageContent = start > end ? List.of() : dtos.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    private ServicoExtraDto toDto(ServicoExtra s) {
        ServicoExtraDto d = new ServicoExtraDto();
        d.setId(s.getId());
        d.setEstadiaId(s.getEstadia() != null ? s.getEstadia().getId() : null);
        d.setTipo(s.getTipo());
        d.setCusto(s.getCusto());
        d.setDataHora(s.getDataHora());
        return d;
    }
}
