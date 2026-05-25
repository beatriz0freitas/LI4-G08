package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeDto;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeFormDto;
import pt.hotel.animais.model.AlteracaoEstadoSaude;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.EstadoSaude;
import pt.hotel.animais.repository.AlteracaoEstadoSaudeRepository;
import pt.hotel.animais.repository.EstadiaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlteracaoEstadoSaudeServiceTest {

    @Mock
    private AlteracaoEstadoSaudeRepository repository;

    @Mock
    private EstadiaRepository estadiaRepository;

    @InjectMocks
    private AlteracaoEstadoSaudeService service;

    @Test
    void registerDevePersistirAlteracaoComSeveridade() {
        Estadia estadia = criarEstadia(1L);
        AlteracaoEstadoSaudeFormDto form = criarForm(1L, "Vómitos frequentes", "CRITICO");

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(repository.save(any(AlteracaoEstadoSaude.class))).thenAnswer(inv -> {
            AlteracaoEstadoSaude a = inv.getArgument(0);
            a.setId(5L);
            return a;
        });

        AlteracaoEstadoSaudeDto resultado = service.register(form);

        assertThat(resultado.getId()).isEqualTo(5L);
        assertThat(resultado.getEstadiaId()).isEqualTo(1L);
        assertThat(resultado.getDescricao()).isEqualTo("Vómitos frequentes");
        assertThat(resultado.getSeveridade()).isEqualTo("CRITICO");
        verify(repository).save(any(AlteracaoEstadoSaude.class));
    }

    @Test
    void registerDeveAceitarSeveridadeEmMinusculas() {
        Estadia estadia = criarEstadia(1L);
        AlteracaoEstadoSaudeFormDto form = criarForm(1L, "Letargia", "normal");

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(repository.save(any(AlteracaoEstadoSaude.class))).thenAnswer(inv -> {
            AlteracaoEstadoSaude a = inv.getArgument(0);
            a.setId(6L);
            a.setSeveridade(EstadoSaude.NORMAL);
            return a;
        });

        AlteracaoEstadoSaudeDto resultado = service.register(form);

        assertThat(resultado.getSeveridade()).isEqualTo("NORMAL");
    }

    @Test
    void registerDeveRejeitarSeveridadeInvalida() {
        Estadia estadia = criarEstadia(1L);
        AlteracaoEstadoSaudeFormDto form = criarForm(1L, "Descrição", "INVALIDO_XYZ");

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));

        assertThatThrownBy(() -> service.register(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Severidade inválida");

        verify(repository, never()).save(any());
    }

    @Test
    void registerDeveAceitarSeveridadeNula() {
        Estadia estadia = criarEstadia(1L);
        AlteracaoEstadoSaudeFormDto form = criarForm(1L, "Observação", null);

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(repository.save(any(AlteracaoEstadoSaude.class))).thenAnswer(inv -> {
            AlteracaoEstadoSaude a = inv.getArgument(0);
            a.setId(7L);
            return a;
        });

        AlteracaoEstadoSaudeDto resultado = service.register(form);

        assertThat(resultado.getId()).isEqualTo(7L);
        assertThat(resultado.getSeveridade()).isNull();
    }

    @Test
    void registerDeveLancarExcecaoSeEstadiaNaoExistir() {
        AlteracaoEstadoSaudeFormDto form = criarForm(99L, "Descrição", "NORMAL");
        when(estadiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.register(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estadia");

        verify(repository, never()).save(any());
    }

    @Test
    void listByEstadiaDeveRetornarPaginaCorrecta() {
        AlteracaoEstadoSaude a = criarAlteracao(1L, criarEstadia(1L));
        when(repository.findByEstadiaIdOrderByDataHoraDesc(1L)).thenReturn(List.of(a));

        Page<AlteracaoEstadoSaudeDto> resultado = service.listByEstadia(1L, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().getFirst().getDescricao()).isEqualTo("Alteração 1");
    }

    @Test
    void listByEstadiaComOffsetForaDoRangeRetornaVazio() {
        AlteracaoEstadoSaude a = criarAlteracao(1L, criarEstadia(1L));
        when(repository.findByEstadiaIdOrderByDataHoraDesc(1L)).thenReturn(List.of(a));

        Page<AlteracaoEstadoSaudeDto> resultado = service.listByEstadia(1L, PageRequest.of(5, 10));

        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }

    private Estadia criarEstadia(Long id) {
        Estadia e = new Estadia();
        e.setId(id);
        e.setEstado(EstadoEstadia.EM_CURSO);
        e.setDataInicio(LocalDateTime.now().minusHours(1));
        return e;
    }

    private AlteracaoEstadoSaudeFormDto criarForm(Long estadiaId, String descricao, String severidade) {
        AlteracaoEstadoSaudeFormDto form = new AlteracaoEstadoSaudeFormDto();
        form.setEstadiaId(estadiaId);
        form.setDescricao(descricao);
        form.setSeveridade(severidade);
        form.setDataHora(LocalDateTime.now());
        return form;
    }

    private AlteracaoEstadoSaude criarAlteracao(Long id, Estadia estadia) {
        AlteracaoEstadoSaude a = new AlteracaoEstadoSaude();
        a.setId(id);
        a.setEstadia(estadia);
        a.setDescricao("Alteração " + id);
        a.setDataHora(LocalDateTime.now());
        return a;
    }
}
