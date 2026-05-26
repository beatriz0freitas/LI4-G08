package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LimpezaServiceTest {
    @Mock
    private AlojamentoRepository alojamentoRepository;

    @Mock
    private AuditoriaOperacaoService auditoriaOperacaoService;

    @InjectMocks
    private LimpezaService limpezaService;

    @Test
    void listarAlojamentosPendentes_deveRetornarAlojamentosPendentes() {
        var alojamento = new Alojamento(1L, "Box 1", null, null, EstadoLimpeza.PENDENTE, null);
        when(alojamentoRepository.findByEstadoLimpeza(EstadoLimpeza.PENDENTE))
                .thenReturn(List.of(alojamento));
        var result = limpezaService.listarAlojamentosPendentes();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdentificacao()).isEqualTo("Box 1");
    }

    @Test
    void marcarComoLimpo_deveAtualizarEstadoParaConcluido() {
        var alojamento = new Alojamento(2L, "Box 2", null, null, EstadoLimpeza.PENDENTE, null);
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
