package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.EstadoReserva;
import pt.hotel.animais.repository.ReservaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceUnitTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private ITutorService tutorService;
    @Mock
    private IAnimalService animalService;
    @Mock
    private IAvailabilityDomainService availabilityDomainService;

    @InjectMocks
    private ReservaService service;

    @Test
    void obterDeveRetornarReservaExistente() {
        Reserva r = reserva(1L, EstadoReserva.ATIVA);
        when(reservaRepository.findWithDetalhesById(1L)).thenReturn(Optional.of(r));

        Reserva resultado = service.obter(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    void obterDeveLancarExcecaoSeNaoEncontrada() {
        when(reservaRepository.findWithDetalhesById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obter(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    void procurarPorTutorDeveDelegarNoRepositorio() {
        when(reservaRepository.findByTutorId(1L)).thenReturn(List.of(reserva(1L, EstadoReserva.ATIVA)));

        List<Reserva> resultado = service.procurarPorTutor(1L);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void procurarPorAnimalDeveDelegarNoRepositorio() {
        when(reservaRepository.findByAnimalId(2L)).thenReturn(List.of());

        List<Reserva> resultado = service.procurarPorAnimal(2L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void procurarAtivasDeveDelegarNoRepositorio() {
        when(reservaRepository.findByTutorIdOrderByDataCriacaoDesc(1L))
                .thenReturn(List.of(reserva(1L, EstadoReserva.ATIVA)));

        List<Reserva> resultado = service.procurarAtivas(1L);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void procurarPorAlojamentoDeveDelegarNoRepositorio() {
        when(reservaRepository.findByAlojamentoId(3L)).thenReturn(List.of());

        List<Reserva> resultado = service.procurarPorAlojamento(3L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void concluirDeveAlterarEstadoParaConcluida() {
        Reserva r = reserva(1L, EstadoReserva.ATIVA);
        when(reservaRepository.findWithDetalhesById(1L)).thenReturn(Optional.of(r));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reserva resultado = service.concluir(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoReserva.CONCLUIDA);
        verify(reservaRepository).save(r);
    }

    @Test
    void concluirDeveRejeitarReservaNaoAtiva() {
        Reserva r = reserva(1L, EstadoReserva.CANCELADA);
        when(reservaRepository.findWithDetalhesById(1L)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> service.concluir(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ativas");
    }

    @Test
    void cancelarDeveAlterarEstadoParaCancelada() {
        Reserva r = reserva(1L, EstadoReserva.ATIVA);
        when(reservaRepository.findWithDetalhesById(1L)).thenReturn(Optional.of(r));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reserva resultado = service.cancelar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    void cancelarDeveRejeitarReservaNaoCancelavel() {
        Reserva r = reserva(1L, EstadoReserva.CONCLUIDA);
        when(reservaRepository.findWithDetalhesById(1L)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> service.cancelar(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void contarReservasAtivasDeveDelegarNoRepositorio() {
        when(reservaRepository.countByEstado(EstadoReserva.ATIVA)).thenReturn(5L);

        long resultado = service.contarReservasAtivas();

        assertThat(resultado).isEqualTo(5L);
    }

    @Test
    void contarReservasFuturasDeveDelegarNoRepositorio() {
        when(reservaRepository.countFuturas(any(LocalDate.class))).thenReturn(3L);

        long resultado = service.contarReservasFuturas();

        assertThat(resultado).isEqualTo(3L);
    }

    @Test
    void listarTodasDeveDelegarNoRepositorio() {
        when(reservaRepository.findAllWithDetalhes()).thenReturn(List.of(reserva(1L, EstadoReserva.ATIVA)));

        List<Reserva> resultado = service.listarTodas();

        assertThat(resultado).hasSize(1);
    }

    private Reserva reserva(Long id, EstadoReserva estado) {
        Reserva r = new Reserva();
        r.setId(id);
        r.setEstado(estado);
        r.setDataInicio(LocalDate.now().plusDays(1));
        r.setDataFim(LocalDate.now().plusDays(3));
        return r;
    }
}
