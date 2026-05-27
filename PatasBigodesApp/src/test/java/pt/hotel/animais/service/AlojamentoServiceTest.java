package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.ReservaRepository;

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

    @Mock
    private IAvailabilityDomainService availabilityDomainService;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private EstadiaRepository estadiaRepository;

    @InjectMocks
    private AlojamentoService alojamentoService;

    @Test
    void contarAlojamentosDisponiveisDeveContarConcluidos() {
        when(alojamentoRepository.countDisponiveisOperacionais()).thenReturn(2L);

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
    void estaDisponivelComEspecieDeveDelegarRegraCentralizada() {
        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = dataInicio.plusDays(2);
        when(availabilityDomainService.estaDisponivel(1L, dataInicio, dataFim, Especie.CAO)).thenReturn(false);

        boolean resultado = alojamentoService.estaDisponivel(
                1L, dataInicio, dataFim, Especie.CAO);

        assertThat(resultado).isFalse();
        verify(availabilityDomainService).estaDisponivel(1L, dataInicio, dataFim, Especie.CAO);
    }

    @Test
    void consultarMapaDeveExporEstadosOperacionaisEFiltrarTipo() {
        LocalDate inicio = LocalDate.now();
        LocalDate fim = inicio.plusDays(2);
        Alojamento livre = new Alojamento(1L, "C01", "CANIL", 1, EstadoLimpeza.CONCLUIDO, null);
        Alojamento ocupado = new Alojamento(2L, "C02", "CANIL", 1, EstadoLimpeza.CONCLUIDO, null);
        Alojamento reservado = new Alojamento(3L, "C03", "CANIL", 1, EstadoLimpeza.CONCLUIDO, null);
        Alojamento limpeza = new Alojamento(4L, "G01", "GATIL", 1, EstadoLimpeza.PENDENTE, null);
        when(alojamentoRepository.findAllByOrderByIdentificacaoAsc())
            .thenReturn(List.of(livre, ocupado, reservado, limpeza));

        Estadia estadia = new Estadia();
        estadia.setId(20L);
        estadia.setEstado(EstadoEstadia.EM_CURSO);
        Reserva reservaOcupada = new Reserva();
        Animal animalOcupado = new Animal();
        animalOcupado.setNome("Luna");
        reservaOcupada.setAnimal(animalOcupado);
        estadia.setReserva(reservaOcupada);
        when(estadiaRepository.findEmCursoPorAlojamentoComDetalhes(any(Long.class))).thenAnswer(invocation -> {
            Long alojamentoId = invocation.getArgument(0);
            return alojamentoId.equals(2L) ? Optional.of(estadia) : Optional.empty();
        });

        Reserva reservaFutura = new Reserva();
        reservaFutura.setId(30L);
        reservaFutura.setDataInicio(inicio);
        reservaFutura.setDataFim(fim);
        Animal animalReservado = new Animal();
        animalReservado.setNome("Max");
        reservaFutura.setAnimal(animalReservado);
        when(reservaRepository.findActiveReservasInPeriodWithDetalhes(any(Long.class), eq(inicio), eq(fim)))
            .thenAnswer(invocation -> {
                Long alojamentoId = invocation.getArgument(0);
                return alojamentoId.equals(3L) ? List.of(reservaFutura) : List.of();
            });

        var mapa = alojamentoService.consultarMapaDisponibilidade(inicio, fim, null);

        assertThat(mapa).extracting("estado").containsExactly("LIVRE", "OCUPADO", "RESERVADO", "LIMPEZA");
        assertThat(mapa.get(1).getAnimalNome()).isEqualTo("Luna");
        assertThat(mapa.get(2).getReservaId()).isEqualTo(30L);

        var filtrado = alojamentoService.consultarMapaDisponibilidade(inicio, fim, "GATIL");
        assertThat(filtrado).hasSize(1);
        assertThat(filtrado.get(0).getEstado()).isEqualTo("LIMPEZA");
    }
}
