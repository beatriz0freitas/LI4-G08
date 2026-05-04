package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class LimpezaServiceTest {
    @Mock
    private AlojamentoRepository alojamentoRepository;

    @InjectMocks
    private LimpezaService limpezaService;

    public LimpezaServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarAlojamentosPendentes_deveRetornarAlojamentosPendentes() {
        var alojamento = Alojamento.builder()
                .id(1L)
                .identificacao("Box 1")
                .estadoLimpeza(EstadoLimpeza.PENDENTE)
                .build();
        when(alojamentoRepository.findByEstadoLimpeza(EstadoLimpeza.PENDENTE))
                .thenReturn(List.of(alojamento));
        var result = limpezaService.listarAlojamentosPendentes();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdentificacao()).isEqualTo("Box 1");
    }

    @Test
    void marcarComoLimpo_deveAtualizarEstadoParaConcluido() {
        var alojamento = Alojamento.builder()
                .id(2L)
                .identificacao("Box 2")
                .estadoLimpeza(EstadoLimpeza.PENDENTE)
                .build();
        when(alojamentoRepository.findById(2L)).thenReturn(Optional.of(alojamento));
        when(alojamentoRepository.save(any())).thenReturn(alojamento);
        boolean sucesso = limpezaService.marcarComoLimpo(2L);
        assertThat(sucesso).isTrue();
        assertThat(alojamento.getEstadoLimpeza()).isEqualTo(EstadoLimpeza.CONCLUIDO);
        verify(alojamentoRepository).save(alojamento);
    }

    @Test
    void marcarComoLimpo_idInexistente_deveRetornarFalse() {
        when(alojamentoRepository.findById(anyLong())).thenReturn(Optional.empty());
        boolean sucesso = limpezaService.marcarComoLimpo(99L);
        assertThat(sucesso).isFalse();
        verify(alojamentoRepository, never()).save(any());
    }
}
