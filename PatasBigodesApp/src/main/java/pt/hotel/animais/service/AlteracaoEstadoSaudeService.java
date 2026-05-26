package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeDto;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeFormDto;
import pt.hotel.animais.model.AlteracaoEstadoSaude;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoSaude;
import pt.hotel.animais.model.enums.PrioridadePlano;
import pt.hotel.animais.repository.AlteracaoEstadoSaudeRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.PlanoCuidadosRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlteracaoEstadoSaudeService implements IAlteracaoEstadoSaudeService {

    private final AlteracaoEstadoSaudeRepository repository;
    private final EstadiaRepository estadiaRepository;
    private final PlanoCuidadosRepository planoCuidadosRepository;
    private final IPlanoCuidadosService planoCuidadosService;

    @Transactional
    public AlteracaoEstadoSaudeDto register(AlteracaoEstadoSaudeFormDto form) {
        Estadia estadia = estadiaRepository.findById(form.getEstadiaId())
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        AlteracaoEstadoSaude a = new AlteracaoEstadoSaude();
        a.setEstadia(estadia);
        a.setDescricao(form.getDescricao());
        if (form.getSeveridade() != null) {
            try {
                a.setSeveridade(EstadoSaude.valueOf(form.getSeveridade().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Severidade inválida: " + form.getSeveridade());
            }
        }
        a.setDataHora(form.getDataHora());

        AlteracaoEstadoSaude saved = repository.save(a);

        // Hook de escalação automática de prioridade quando severidade é CRITICO
        if (a.getSeveridade() == EstadoSaude.CRITICO) {
            try {
                planoCuidadosRepository.findByEstadiaId(estadia.getId()).ifPresent(plano -> {
                    try {
                        log.info("Escalando prioridade do plano {} para CRITICO", plano.getId());
                        planoCuidadosService.atualizarPrioridade(plano.getId(), PrioridadePlano.CRITICO, 
                                                                 a.getAutorId() != null ? a.getAutorId() : 1L);
                    } catch (Exception e) {
                        log.error("Erro ao escalizar prioridade de plano na mudança de saúde CRITICA: {}", e.getMessage());
                    }
                });
            } catch (Exception e) {
                log.error("Erro ao executar hook de prioridade: {}", e.getMessage());
            }
        }

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<AlteracaoEstadoSaudeDto> listByEstadia(Long estadiaId, Pageable pageable) {
        List<AlteracaoEstadoSaude> list = repository.findByEstadiaIdOrderByDataHoraDesc(estadiaId);
        List<AlteracaoEstadoSaudeDto> dtos = list.stream().map(this::toDto).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        List<AlteracaoEstadoSaudeDto> pageContent = start > end ? List.of() : dtos.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    private AlteracaoEstadoSaudeDto toDto(AlteracaoEstadoSaude a) {
        AlteracaoEstadoSaudeDto d = new AlteracaoEstadoSaudeDto();
        d.setId(a.getId());
        d.setEstadiaId(a.getEstadia() != null ? a.getEstadia().getId() : null);
        d.setDescricao(a.getDescricao());
        d.setSeveridade(a.getSeveridade() != null ? a.getSeveridade().name() : null);
        d.setDataHora(a.getDataHora());
        return d;
    }
}
