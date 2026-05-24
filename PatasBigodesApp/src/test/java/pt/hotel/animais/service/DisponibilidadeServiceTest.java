package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.dto.DisponibilidadeAlojamentoDto;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.TipoAlojamento;
import pt.hotel.animais.repository.AlojamentoRepository;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisponibilidadeServiceTest {

    @Mock
    private AlojamentoRepository alojamentoRepository;

    @InjectMocks
    private AlojamentoService alojamentoService;

    @Test
    void consultarDisponibilidadeDeveConverterAlojamentosDisponiveis() {
        LocalDate dataInicio = LocalDate.now().plusDays(3);
        LocalDate dataFim = dataInicio.plusDays(2);
        Alojamento alojamento = criarAlojamento(1L, "Box 01", TipoAlojamento.CANINO);

        when(alojamentoRepository.findAvailableForPeriod(dataInicio, dataFim)).thenReturn(List.of(alojamento));

        List<DisponibilidadeAlojamentoDto> resultado = alojamentoService.consultarDisponibilidade(dataInicio, dataFim);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getAlojamentoId()).isEqualTo(1L);
        assertThat(resultado.get(0).getIdentificacao()).isEqualTo("Box 01");
        assertThat(resultado.get(0).getTipo()).isEqualTo(TipoAlojamento.CANINO);
        assertThat(resultado.get(0).getDataInicio()).isEqualTo(dataInicio);
        assertThat(resultado.get(0).getDataFim()).isEqualTo(dataFim);
        assertThat(resultado.get(0).isDisponivel()).isTrue();

        verify(alojamentoRepository).findAvailableForPeriod(dataInicio, dataFim);
    }

    @Test
    void consultarDisponibilidadeComEspecieDeveFiltrarPorTipo() {
        LocalDate dataInicio = LocalDate.now().plusDays(5);
        LocalDate dataFim = dataInicio.plusDays(1);
        Alojamento alojamento = criarAlojamento(2L, "Box CAN-02", TipoAlojamento.CANINO);

        when(alojamentoRepository.findAvailableForPeriodAndTipo(dataInicio, dataFim, TipoAlojamento.CANINO))
            .thenReturn(List.of(alojamento));

        List<DisponibilidadeAlojamentoDto> resultado = alojamentoService.consultarDisponibilidade(dataInicio, dataFim, Especie.CAO);

        assertThat(resultado).extracting(DisponibilidadeAlojamentoDto::getAlojamentoId).containsExactly(2L);
        verify(alojamentoRepository).findAvailableForPeriodAndTipo(dataInicio, dataFim, TipoAlojamento.CANINO);
    }

    @Test
    void estaDisponivelDeveRetornarFalsoQuandoLimpezaNaoEstaConcluida() {
        Alojamento alojamento = criarAlojamento(3L, "Box 03", TipoAlojamento.CANINO);
        LocalDate dataInicio = LocalDate.now().plusDays(7);
        LocalDate dataFim = dataInicio.plusDays(2);

        when(alojamentoRepository.findById(3L)).thenReturn(Optional.of(alojamento));
        when(alojamentoRepository.countConflictingReservas(3L, dataInicio, dataFim)).thenReturn(1L);

        boolean disponivel = alojamentoService.estaDisponivel(3L, dataInicio, dataFim, Especie.CAO);

        assertThat(disponivel).isFalse();
    }

    @Test
    void consultarDisponibilidadeComDatasInvalidasDeveLancarExcecao() {
        LocalDate dataInicio = LocalDate.now().plusDays(2);
        LocalDate dataFim = dataInicio.minusDays(1);

        assertThatThrownBy(() -> alojamentoService.consultarDisponibilidade(dataInicio, dataFim))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("dataInicio deve ser anterior a dataFim");
    }

    private Alojamento criarAlojamento(Long id, String identificacao, TipoAlojamento tipo) {
        Alojamento alojamento = new Alojamento();
        definirCampo(alojamento, "id", id);
        definirCampo(alojamento, "identificacao", identificacao);
        definirCampo(alojamento, "tipo", tipo);
        definirCampo(alojamento, "capacidade", 2);
        definirCampo(alojamento, "estadoLimpeza", EstadoLimpeza.CONCLUIDO);
        return alojamento;
    }

    private void definirCampo(Alojamento alojamento, String nomeCampo, Object valor) {
        try {
            Field field = Alojamento.class.getDeclaredField(nomeCampo);
            field.setAccessible(true);
            field.set(alojamento, valor);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Nao foi possivel preparar o alojamento de teste", exception);
        }
    }
}
