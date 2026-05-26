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
import pt.hotel.animais.model.TipoServicoExtra;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.repository.ServicoExtraRepository;
import pt.hotel.animais.repository.EstadiaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicoExtraService implements IServicoExtraService {

    private final ServicoExtraRepository servicoExtraRepository;
    private final EstadiaRepository estadiaRepository;
    private final IPagamentoService pagamentoService;
    private final TipoServicoExtraService tipoServicoExtraService;

    /**
     * Registar um serviço extra para uma estadia em curso.
     * O tipo é carregado do catálogo gerido pelo diretor.
     */
    public ServicoExtraDto register(ServicoExtraFormDto req, Long autorId) {
        Estadia estadia = estadiaRepository.findById(req.getEstadiaId())
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        if (estadia.getEstado() != pt.hotel.animais.model.enums.EstadoEstadia.EM_CURSO) {
            throw new IllegalArgumentException("Só é possível registar serviços extra para estadias em curso");
        }

        // Carregar tipo de serviço pelo nome
        TipoServicoExtra tipo = tipoServicoExtraService.obterPorNome(req.getTipo())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de serviço não encontrado: " + req.getTipo()));

        if (!tipo.getAtivo()) {
            throw new IllegalArgumentException("Tipo de serviço inativo: " + req.getTipo());
        }

        ServicoExtra se = new ServicoExtra();
        se.setEstadia(estadia);
        se.setTipoServicoExtra(tipo);
        se.setCusto(req.getCusto());
        se.setDataHora(req.getDataHora());
        se.setAutorId(autorId);

        ServicoExtra saved = servicoExtraRepository.save(se);

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

    /**
     * Converter ServicoExtra para DTO.
     * Extrai o nome do tipo de serviço do objeto TipoServicoExtra.
     */
    private ServicoExtraDto toDto(ServicoExtra s) {
        ServicoExtraDto d = new ServicoExtraDto();
        d.setId(s.getId());
        d.setEstadiaId(s.getEstadia() != null ? s.getEstadia().getId() : null);
        d.setTipo(s.getTipoServicoExtra() != null ? s.getTipoServicoExtra().getNome() : null);
        d.setCusto(s.getCusto());
        d.setDataHora(s.getDataHora());
        return d;
    }
}
