package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pt.hotel.animais.dto.ServicoExtraDto;
import pt.hotel.animais.dto.ServicoExtraFormDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.ServicoExtra;
import pt.hotel.animais.model.TipoServicoExtra;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoExtraServiceTest {

    @Mock
    private ServicoExtraRepository servicoExtraRepository;

    @Mock
    private EstadiaRepository estadiaRepository;

    @Mock
    private IPagamentoService pagamentoService;

    @Mock
    private TipoServicoExtraService tipoServicoExtraService;

    @InjectMocks
    private ServicoExtraService service;

    @Test
    void registerDeveGuardarServicoExtraParaEstadiaEmCurso() {
        Estadia estadia = criarEstadia(1L, EstadoEstadia.EM_CURSO);
        ServicoExtraFormDto form = criarForm(1L, "Banho", new BigDecimal("15.00"));

        TipoServicoExtra tipoServico = new TipoServicoExtra("Banho", "Banho do animal");
        try {
            var tipoIdField = tipoServico.getClass().getDeclaredField("id");
            tipoIdField.setAccessible(true);
            tipoIdField.set(tipoServico, 200L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tipoServico.setAtivo(true);

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(tipoServicoExtraService.obterPorNome("Banho")).thenReturn(Optional.of(tipoServico));
        when(servicoExtraRepository.save(any(ServicoExtra.class))).thenAnswer(inv -> {
            ServicoExtra se = inv.getArgument(0);
            se.setId(5L);
            return se;
        });
        ServicoExtraDto resultado = service.register(form, 2L);

        assertThat(resultado.getId()).isEqualTo(5L);
        assertThat(resultado.getTipo()).isEqualTo("Banho");
        assertThat(resultado.getCusto()).isEqualByComparingTo("15.00");
        assertThat(resultado.getEstadiaId()).isEqualTo(1L);
        verify(servicoExtraRepository).save(any(ServicoExtra.class));
    }

    @Test
    void registerDeveRejeitarEstadiaTerminada() {
        Estadia estadia = criarEstadia(1L, EstadoEstadia.TERMINADA);
        ServicoExtraFormDto form = criarForm(1L, "Passeio", new BigDecimal("10.00"));

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));

        assertThatThrownBy(() -> service.register(form, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("em curso");

        verify(servicoExtraRepository, never()).save(any());
    }

    @Test
    void registerDeveRejeitarCustoNegativo() {
        Estadia estadia = criarEstadia(1L, EstadoEstadia.EM_CURSO);
        ServicoExtraFormDto form = criarForm(1L, "Passeio", new BigDecimal("-1.00"));

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));

        assertThatThrownBy(() -> service.register(form, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pode ser negativo");

        verify(servicoExtraRepository, never()).save(any());
    }

    @Test
    void registerDeveLancarExcecaoSeEstadiaNaoExistir() {
        ServicoExtraFormDto form = criarForm(99L, "Tosa", new BigDecimal("20.00"));
        when(estadiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.register(form, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estadia");

        verify(servicoExtraRepository, never()).save(any());
    }

    @Test
    void registerNaoFalhaSeCalcularExtrasFalhar() {
        Estadia estadia = criarEstadia(1L, EstadoEstadia.EM_CURSO);
        ServicoExtraFormDto form = criarForm(1L, "Consulta", new BigDecimal("30.00"));

        TipoServicoExtra tipoServico = new TipoServicoExtra("Consulta", "Consulta veterinária");
        try {
            var tipoIdField = tipoServico.getClass().getDeclaredField("id");
            tipoIdField.setAccessible(true);
            tipoIdField.set(tipoServico, 201L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tipoServico.setAtivo(true);

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(tipoServicoExtraService.obterPorNome("Consulta")).thenReturn(Optional.of(tipoServico));
        when(servicoExtraRepository.save(any(ServicoExtra.class))).thenAnswer(inv -> {
            ServicoExtra se = inv.getArgument(0);
            se.setId(6L);
            return se;
        });
        ServicoExtraDto resultado = service.register(form, 1L);

        assertThat(resultado.getId()).isEqualTo(6L);
    }

    @Test
    void listByEstadiaDeveRetornarPaginaComServicos() {
        ServicoExtra se = criarServicoExtra(1L, criarEstadia(1L, EstadoEstadia.EM_CURSO));
        when(servicoExtraRepository.findByEstadiaId(1L)).thenReturn(List.of(se));

        Page<ServicoExtraDto> resultado = service.listByEstadia(1L, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().getFirst().getTipo()).isEqualTo("Tipo 1");
    }

    @Test
    void listByEstadiaComOffsetForaDoRangeRetornaVazio() {
        ServicoExtra se = criarServicoExtra(1L, criarEstadia(1L, EstadoEstadia.EM_CURSO));
        when(servicoExtraRepository.findByEstadiaId(1L)).thenReturn(List.of(se));

        Page<ServicoExtraDto> resultado = service.listByEstadia(1L, PageRequest.of(5, 10));

        assertThat(resultado.getContent()).isEmpty();
    }

    private Estadia criarEstadia(Long id, EstadoEstadia estado) {
        Estadia e = new Estadia();
        e.setId(id);
        e.setEstado(estado);
        e.setDataInicio(LocalDateTime.now().minusHours(2));
        return e;
    }

    private ServicoExtraFormDto criarForm(Long estadiaId, String tipo, BigDecimal custo) {
        ServicoExtraFormDto form = new ServicoExtraFormDto();
        form.setEstadiaId(estadiaId);
        form.setTipo(tipo);
        form.setCusto(custo);
        form.setDataHora(LocalDateTime.now());
        return form;
    }

    private ServicoExtra criarServicoExtra(Long id, Estadia estadia) {
        ServicoExtra se = new ServicoExtra();
        se.setId(id);
        se.setEstadia(estadia);
        
        TipoServicoExtra tipo = new TipoServicoExtra("Tipo " + id, "Descrição");
        try {
            var tipoIdField = tipo.getClass().getDeclaredField("id");
            tipoIdField.setAccessible(true);
            tipoIdField.set(tipo, id * 100L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        se.setTipoServicoExtra(tipo);
        se.setCusto(new BigDecimal("10.00"));
        se.setDataHora(LocalDateTime.now());
        return se;
    }
}
