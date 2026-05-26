package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pt.hotel.animais.dto.RegistoCuidadoDto;
import pt.hotel.animais.dto.RegistoCuidadoFormDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.RegistoCuidado;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.RegistoCuidadoRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistoCuidadoServiceTest {

    @Mock
    private RegistoCuidadoRepository registoCuidadoRepository;

    @Mock
    private EstadiaRepository estadiaRepository;

    @Mock
    private AuditoriaOperacaoService auditoriaOperacaoService;

    @InjectMocks
    private RegistoCuidadoService registoCuidadoService;

    @Test
    void createDeveRegistarCuidadoParaEstadiaEmCurso() {
        Estadia estadia = criarEstadia(1L, EstadoEstadia.EM_CURSO);
        RegistoCuidadoFormDto form = criarForm(1L, "Banho dado", LocalDateTime.now());

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(registoCuidadoRepository.save(any(RegistoCuidado.class))).thenAnswer(inv -> {
            RegistoCuidado rc = inv.getArgument(0);
            rc.setId(10L);
            return rc;
        });

        RegistoCuidadoDto resultado = registoCuidadoService.create(form, 5L);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getDescricao()).isEqualTo("Banho dado");
        assertThat(resultado.getEstadiaId()).isEqualTo(1L);
        assertThat(resultado.getDataHora()).isNotNull();
        assertThat(resultado.getAutorNome()).isEqualTo("5");
        verify(registoCuidadoRepository).save(any(RegistoCuidado.class));
    }

    @Test
    void createDeveRejeitarEstadiaQueNaoEstaEmCurso() {
        Estadia estadia = criarEstadia(1L, EstadoEstadia.TERMINADA);
        RegistoCuidadoFormDto form = criarForm(1L, "Banho", LocalDateTime.now());

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));

        assertThatThrownBy(() -> registoCuidadoService.create(form, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("em curso");

        verify(registoCuidadoRepository, never()).save(any());
    }

    @Test
    void createDeveLancarExcecaoParaEstadiaInexistente() {
        RegistoCuidadoFormDto form = criarForm(99L, "Alimentação", LocalDateTime.now());
        when(estadiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registoCuidadoService.create(form, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estadia");
    }

    @Test
    void listByEstadiaDeveRetornarPaginaComRegistos() {
        RegistoCuidado rc = criarRegistoCuidado(1L, criarEstadia(1L, EstadoEstadia.EM_CURSO));
        when(registoCuidadoRepository.findByEstadiaIdOrderByDataHoraDesc(1L)).thenReturn(List.of(rc));

        Page<RegistoCuidadoDto> resultado = registoCuidadoService.listByEstadia(1L, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().getFirst().getDescricao()).isEqualTo("Cuidado 1");
    }

    @Test
    void listByEstadiaComPaginacaoOffsetForaDoRangeRetornaVazio() {
        RegistoCuidado rc = criarRegistoCuidado(1L, criarEstadia(1L, EstadoEstadia.EM_CURSO));
        when(registoCuidadoRepository.findByEstadiaIdOrderByDataHoraDesc(1L)).thenReturn(List.of(rc));

        // pede página 5 (offset=50) com apenas 1 registo
        Page<RegistoCuidadoDto> resultado = registoCuidadoService.listByEstadia(1L, PageRequest.of(5, 10));

        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }

    private Estadia criarEstadia(Long id, EstadoEstadia estado) {
        Estadia estadia = new Estadia();
        estadia.setId(id);
        estadia.setEstado(estado);
        estadia.setDataInicio(LocalDateTime.now().minusHours(2));
        return estadia;
    }

    private RegistoCuidadoFormDto criarForm(Long estadiaId, String descricao, LocalDateTime dataHora) {
        RegistoCuidadoFormDto form = new RegistoCuidadoFormDto();
        form.setEstadiaId(estadiaId);
        form.setDescricao(descricao);
        form.setDataHora(dataHora);
        return form;
    }

    private RegistoCuidado criarRegistoCuidado(Long id, Estadia estadia) {
        RegistoCuidado rc = new RegistoCuidado();
        rc.setId(id);
        rc.setEstadia(estadia);
        rc.setDescricao("Cuidado " + id);
        rc.setDataHora(LocalDateTime.now());
        rc.setAutorId(1L);
        return rc;
    }
}
