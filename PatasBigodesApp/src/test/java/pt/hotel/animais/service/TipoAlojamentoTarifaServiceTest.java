package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.model.TipoAlojamentoTarifa;
import pt.hotel.animais.repository.TipoAlojamentoTarifaRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoAlojamentoTarifaServiceTest {

    @Mock
    private TipoAlojamentoTarifaRepository repository;

    @InjectMocks
    private TipoAlojamentoTarifaService service;

    @Test
    void obterValorTarifaUsaTipoNormalizadoEAtivo() {
        TipoAlojamentoTarifa tarifa = new TipoAlojamentoTarifa("CANINO", new BigDecimal("15.00"));
        when(repository.findActivoByTipo("CANINO")).thenReturn(Optional.of(tarifa));

        BigDecimal valor = service.obterValorTarifa(" canino ");

        assertThat(valor).isEqualByComparingTo(new BigDecimal("15.00"));
        verify(repository).findActivoByTipo("CANINO");
    }

    @Test
    void obterValorTarifaFalhaSemTarifaAtiva() {
        when(repository.findActivoByTipo("EXOTICO")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obterValorTarifa("exotico"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tarifa não configurada");
    }

    @Test
    void criarNormalizaTipoEImpedeDuplicados() {
        when(repository.findByTipoAlojamento("FELINO")).thenReturn(Optional.empty());
        when(repository.save(any(TipoAlojamentoTarifa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TipoAlojamentoTarifa criada = service.criar(" felino ", new BigDecimal("10.00"));

        assertThat(criada.getTipoAlojamento()).isEqualTo("FELINO");
        assertThat(criada.getTarifaDiaria()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void criarRejeitaTarifaNegativa() {
        assertThatThrownBy(() -> service.criar("CANINO", new BigDecimal("-1.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativa");

        verify(repository, never()).save(any());
    }

    @Test
    void atualizarTarifaRejeitaValorNegativo() {
        assertThatThrownBy(() -> service.atualizarTarifa(1L, new BigDecimal("-1.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativa");

        verify(repository, never()).findById(any());
    }
}
