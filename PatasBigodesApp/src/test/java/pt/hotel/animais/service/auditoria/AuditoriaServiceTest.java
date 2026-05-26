package pt.hotel.animais.service.auditoria;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pt.hotel.animais.dto.auditoria.AuditoriaFiltroDTO;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.auditoria.AuditoriaEvento;
import pt.hotel.animais.model.enums.ResultadoAuditoria;
import pt.hotel.animais.model.enums.TipoColaborador;
import pt.hotel.animais.repository.AuditoriaRepository;
import pt.hotel.animais.repository.ColaboradorRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditoriaServiceTest {

    private final AuditoriaRepository auditoriaRepository = mock(AuditoriaRepository.class);
    private final ColaboradorRepository colaboradorRepository = mock(ColaboradorRepository.class);
    private final AuditoriaService service = new AuditoriaService(auditoriaRepository, colaboradorRepository);

    @Test
    void registarEventoDevePersistirEventoComDetalhesJson() {
        Colaborador colaborador = colaborador(7L);
        when(colaboradorRepository.findById(7L)).thenReturn(Optional.of(colaborador));
        when(auditoriaRepository.save(any(AuditoriaEvento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> detalhes = new LinkedHashMap<>();
        detalhes.put("campo", "valor");

        AuditoriaEvento evento = service.registarEvento(
            7L,
            "CRIAR_COLABORADOR",
            "Colaborador",
            11L,
            "CREATE",
            detalhes,
            ResultadoAuditoria.SUCESSO,
            null
        );

        assertThat(evento.getUtilizador()).isEqualTo(colaborador);
        assertThat(evento.getOperacao()).isEqualTo("CRIAR_COLABORADOR");
        assertThat(evento.getEntidade()).isEqualTo("Colaborador");
        assertThat(evento.getEntityId()).isEqualTo(11L);
        assertThat(evento.getAcao()).isEqualTo("CREATE");
        assertThat(evento.getDetalhes()).containsEntry("campo", "valor");
        assertThat(evento.getResultado()).isEqualTo(ResultadoAuditoria.SUCESSO);
        verify(auditoriaRepository).save(any(AuditoriaEvento.class));
    }

    @Test
    void registarEventoComUtilizadorNuloDeveFalhar() {
        assertThatThrownBy(() -> service.registarEvento(
            null,
            "CRIAR_COLABORADOR",
            "Colaborador",
            11L,
            "CREATE",
            Map.of(),
            ResultadoAuditoria.SUCESSO,
            null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Utilizador");
    }

    @Test
    void limparEventosAntigosDeveDelegarParaRepositorio() {
        when(auditoriaRepository.deleteByTimestampBefore(any(LocalDateTime.class))).thenReturn(3L);

        long removidos = service.limparzardosAntigos(12);

        assertThat(removidos).isEqualTo(3L);
        verify(auditoriaRepository).deleteByTimestampBefore(any(LocalDateTime.class));
    }

    @Test
    void consultarPorPeriodoDeveAplicarFiltros() {
        Page<AuditoriaEvento> page = new PageImpl<>(List.of());
        when(auditoriaRepository.findAll(anySpecification(), any(Pageable.class)))
            .thenReturn(page);

        AuditoriaFiltroDTO filtros = new AuditoriaFiltroDTO();
        filtros.setUtilizadorId(7L);
        filtros.setOperacao("CRIAR");
        filtros.setEntidade("Colaborador");
        filtros.setResultado(ResultadoAuditoria.SUCESSO);

        Page<AuditoriaEvento> resultado = service.consultarPorPeriodo(
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 1, 31),
            filtros,
            PageRequest.of(0, 10)
        );

        assertThat(resultado).isSameAs(page);
    }

    @Test
    void consultarPorUtilizadorDeveDelegarAoFiltroTemporal() {
        Page<AuditoriaEvento> page = new PageImpl<>(List.of());
        when(auditoriaRepository.findAll(anySpecification(), any(Pageable.class)))
            .thenReturn(page);

        Page<AuditoriaEvento> resultado = service.consultarPorUtilizador(7L, new AuditoriaFiltroDTO(), PageRequest.of(0, 10));

        assertThat(resultado).isSameAs(page);
    }

    private Colaborador colaborador(Long id) {
        Colaborador colaborador = new Colaborador();
        colaborador.setId(id);
        colaborador.setUsername("diretor");
        colaborador.setNome("Diretor Geral");
        colaborador.setEmail("diretor@hotel.local");
        colaborador.setPasswordHash("hash");
        colaborador.setTipoColaborador(TipoColaborador.DIRETOR);
        colaborador.setAtivo(true);
        return colaborador;
    }

    @SuppressWarnings("unchecked")
    private Specification<AuditoriaEvento> anySpecification() {
        return (Specification<AuditoriaEvento>) any(Specification.class);
    }
}
