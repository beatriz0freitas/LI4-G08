package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.TipoAlojamento;
import pt.hotel.animais.repository.AlojamentoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlojamentoServiceTest {

    @Mock
    private AlojamentoRepository alojamentoRepository;

    @InjectMocks
    private AlojamentoService alojamentoService;

    @Test
    void contarAlojamentosDisponiveisDeveContarConcluidos() {
        when(alojamentoRepository.countByEstadoLimpeza(EstadoLimpeza.CONCLUIDO)).thenReturn(2L);

        long resultado = alojamentoService.contarAlojamentosDisponiveis();

        assertThat(resultado).isEqualTo(2L);
    }

    @Test
    void listarTodosDeveDelegarParaOrdenacaoPorIdentificacao() {
        when(alojamentoRepository.findAllByOrderByIdentificacaoAsc()).thenReturn(java.util.List.of(
                    new Alojamento(1L, "Box 1", null, null, EstadoLimpeza.PENDENTE, null)
        ));

        var resultado = alojamentoService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdentificacao()).isEqualTo("Box 1");
    }

    @Test
    void contarAlojamentosPendentesLimpezaDeveDelegarParaRepositorio() {
        when(alojamentoRepository.countByEstadoLimpeza(EstadoLimpeza.PENDENTE)).thenReturn(3L);

        long resultado = alojamentoService.contarAlojamentosPendentesLimpeza();

        assertThat(resultado).isEqualTo(3L);
    }

    @Test
    void obterDeveRetornarAlojamentoExistente() {
        Alojamento alojamento = new Alojamento(1L, "Box 1", null, null, EstadoLimpeza.CONCLUIDO, null);
        when(alojamentoRepository.findById(1L)).thenReturn(Optional.of(alojamento));

        Alojamento resultado = alojamentoService.obter(1L);

        assertThat(resultado.getIdentificacao()).isEqualTo("Box 1");
    }

    @Test
    void obterDeveLancarExcecaoSeNaoEncontrado() {
        when(alojamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alojamentoService.obter(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    void marcarPendenteLimpezaDeveAtualizarEstado() {
        Alojamento alojamento = new Alojamento(1L, "Box 1", null, null, EstadoLimpeza.CONCLUIDO, null);
        when(alojamentoRepository.findById(1L)).thenReturn(Optional.of(alojamento));
        when(alojamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        alojamentoService.marcarPendenteLimpeza(1L);

        assertThat(alojamento.getEstadoLimpeza()).isEqualTo(EstadoLimpeza.PENDENTE);
        verify(alojamentoRepository).save(alojamento);
    }

    @Test
    void marcarLimpezaConcluidaDeveAtualizarEstado() {
        Alojamento alojamento = new Alojamento(1L, "Box 1", null, null, EstadoLimpeza.PENDENTE, null);
        when(alojamentoRepository.findById(1L)).thenReturn(Optional.of(alojamento));
        when(alojamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        alojamentoService.marcarLimpezaConcluida(1L);

        assertThat(alojamento.getEstadoLimpeza()).isEqualTo(EstadoLimpeza.CONCLUIDO);
        verify(alojamentoRepository).save(alojamento);
    }

    @Test
    void consultarDisponibilidadeDeveRejeitarDatasInvalidas() {
        assertThatThrownBy(() -> alojamentoService.consultarDisponibilidade(null, LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> alojamentoService.consultarDisponibilidade(
                LocalDate.now().plusDays(5), LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void consultarDisponibilidadeComEspecieDeveRejeitarDatasInvalidas() {
        assertThatThrownBy(() -> alojamentoService.consultarDisponibilidade(
                null, LocalDate.now(), Especie.CAO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void estaDisponivelComEspecieRetornaFalsoSeTipoIncompativel() {
        Alojamento alojamento = new Alojamento(1L, "Box 1", TipoAlojamento.FELINO, 1, EstadoLimpeza.CONCLUIDO, null);
        when(alojamentoRepository.findById(1L)).thenReturn(Optional.of(alojamento));

        boolean resultado = alojamentoService.estaDisponivel(
                1L, LocalDate.now(), LocalDate.now().plusDays(2), Especie.CAO);

        assertThat(resultado).isFalse();
        verify(alojamentoRepository, never()).countConflictingReservas(any(), any(), any());
    }
}