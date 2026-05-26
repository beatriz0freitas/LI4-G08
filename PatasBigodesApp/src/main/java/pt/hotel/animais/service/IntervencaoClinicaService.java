package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.IntervencaoClinicaDto;
import pt.hotel.animais.dto.IntervencaoClinicaFormDto;
import pt.hotel.animais.model.IntervencaoClinica;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class IntervencaoClinicaService implements IIntervencaoClinicaService {

    private final IntervencaoClinicaRepository intervencaoClinicaRepository;
    private final EstadiaRepository estadiaRepository;
    private final AuditoriaOperacaoService auditoriaOperacaoService;

    @Transactional
    public IntervencaoClinicaDto register(IntervencaoClinicaFormDto form, Long autorId) {
        validarFormulario(form);
        validarMedicoResponsavel(autorId);

        Estadia estadia = estadiaRepository.findById(form.getEstadiaId())
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        if (estadia.getEstado() != EstadoEstadia.EM_CURSO) {
            throw new IllegalArgumentException("Só é possível registar intervenções clínicas durante estadias em curso");
        }

        IntervencaoClinica ic = new IntervencaoClinica();
        ic.setEstadia(estadia);
        ic.setDescricao(form.getDescricao().trim());
        ic.setCusto(form.getCusto());
        ic.setDataHora(form.getDataHora());
        ic.setMedicoId(autorId);

        IntervencaoClinica saved = intervencaoClinicaRepository.save(ic);
        auditoriaOperacaoService.registarSucesso(
            autorId,
            "INTERVENCAO_CLINICA",
            "Intervencao",
            saved.getId(),
            "CREATE",
            detalhesIntervencao(saved)
        );
        return toDto(saved);
    }

    private void validarFormulario(IntervencaoClinicaFormDto form) {
        if (form == null) {
            throw new IllegalArgumentException("Dados da intervenção clínica são obrigatórios");
        }
        if (form.getEstadiaId() == null) {
            throw new IllegalArgumentException("Estadia da intervenção clínica é obrigatória");
        }
        if (form.getDescricao() == null || form.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Descrição da intervenção clínica é obrigatória");
        }
        validarCusto(form.getCusto());
        if (form.getDataHora() == null) {
            throw new IllegalArgumentException("Data/hora da intervenção clínica é obrigatória");
        }
    }

    private void validarMedicoResponsavel(Long autorId) {
        if (autorId == null) {
            throw new IllegalArgumentException("Médico responsável pela intervenção clínica é obrigatório");
        }
        validarAutorizacaoVeterinaria();
    }

    private void validarCusto(BigDecimal custo) {
        if (custo == null) {
            throw new IllegalArgumentException("Custo da intervenção clínica é obrigatório");
        }
        if (custo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Custo da intervenção clínica não pode ser negativo");
        }
    }

    private void validarAutorizacaoVeterinaria() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a -> "ROLE_MEDICO_VETERINARIO".equals(a.getAuthority()))) {
            throw new IllegalArgumentException("Apenas médico veterinário pode registar intervenções clínicas");
        }
    }

    @Transactional(readOnly = true)
    public Page<IntervencaoClinicaDto> listByEstadia(Long estadiaId, Pageable pageable) {
        List<IntervencaoClinica> list = intervencaoClinicaRepository.findByEstadiaId(estadiaId);
        List<IntervencaoClinicaDto> dtos = list.stream().map(this::toDto).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        List<IntervencaoClinicaDto> pageContent = start > end ? List.of() : dtos.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    private IntervencaoClinicaDto toDto(IntervencaoClinica i) {
        IntervencaoClinicaDto d = new IntervencaoClinicaDto();
        d.setId(i.getId());
        d.setEstadiaId(i.getEstadia() != null ? i.getEstadia().getId() : null);
        d.setDescricao(i.getDescricao());
        d.setCusto(i.getCusto());
        d.setDataHora(i.getDataHora());
        return d;
    }

    private Map<String, Object> detalhesIntervencao(IntervencaoClinica intervencao) {
        Map<String, Object> detalhes = new LinkedHashMap<>();
        detalhes.put("estadiaId", intervencao.getEstadia() != null ? intervencao.getEstadia().getId() : null);
        detalhes.put("medicoId", intervencao.getMedicoId());
        detalhes.put("custo", intervencao.getCusto() != null ? intervencao.getCusto().toPlainString() : null);
        detalhes.put("dataHora", intervencao.getDataHora() != null ? intervencao.getDataHora().toString() : null);
        detalhes.put("descricao", intervencao.getDescricao());
        return detalhes;
    }
}
