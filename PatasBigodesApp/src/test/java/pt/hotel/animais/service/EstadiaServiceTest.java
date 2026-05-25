package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.EstadoReserva;
import pt.hotel.animais.repository.EstadiaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadiaServiceTest {

    @Mock
    private EstadiaRepository estadiaRepository;

    @Mock
    private IReservaService reservaService;

    @Mock
    private IPagamentoService pagamentoService;

    @Mock
    private IAlojamentoService alojamentoService;

    @InjectMocks
    private EstadiaService estadiaService;

    @Test
    void abrirEstadiaPorReservaDeveCriarEstadiaEmCurso() {
        Reserva reserva = criarReserva(10L, EstadoReserva.ATIVA);

        when(reservaService.obter(10L)).thenReturn(reserva);
        when(estadiaRepository.save(any(Estadia.class))).thenAnswer(inv -> {
            Estadia e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });
        when(pagamentoService.calcularValorBase(any())).thenReturn(java.math.BigDecimal.TEN);

        Estadia resultado = estadiaService.abrirEstadiaPorReserva(10L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoEstadia.EM_CURSO);
        assertThat(resultado.getDataInicio()).isNotNull();
        verify(reservaService).concluir(10L);
        verify(pagamentoService).registrarPagamento(any());
    }

    @Test
    void abrirEstadiaPorReservaDeveRejeitarReservaNaoAtiva() {
        Reserva reserva = criarReserva(20L, EstadoReserva.CANCELADA);
        when(reservaService.obter(20L)).thenReturn(reserva);

        assertThatThrownBy(() -> estadiaService.abrirEstadiaPorReserva(20L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("check-in");

        verify(estadiaRepository, never()).save(any());
    }

    @Test
    void checkOutDeveTerminarEstadiaEmCurso() {
        Estadia estadia = criarEstadia(5L, EstadoEstadia.EM_CURSO);
        estadia.setReserva(criarReservaComAlojamento());

        when(estadiaRepository.findById(5L)).thenReturn(Optional.of(estadia));
        when(estadiaRepository.save(any(Estadia.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pagamentoService.calcularExtras(any())).thenReturn(java.math.BigDecimal.ZERO);

        Estadia resultado = estadiaService.checkOut(5L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoEstadia.TERMINADA);
        assertThat(resultado.getDataFim()).isNotNull();
    }

    @Test
    void checkOutDeveRejeitarEstadiaJaTerminada() {
        Estadia estadia = criarEstadia(5L, EstadoEstadia.TERMINADA);
        when(estadiaRepository.findById(5L)).thenReturn(Optional.of(estadia));

        assertThatThrownBy(() -> estadiaService.checkOut(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("em curso");
    }

    @Test
    void checkOutDeveLancarExcecaoParaEstadiaInexistente() {
        when(estadiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estadiaService.checkOut(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não encontrada");
    }

    @Test
    void contarEstadiasEmCursoDeveDelegarNoRepositorio() {
        when(estadiaRepository.countByEstado(EstadoEstadia.EM_CURSO)).thenReturn(3L);

        long resultado = estadiaService.contarEstadiasEmCurso();

        assertThat(resultado).isEqualTo(3L);
    }

    private Reserva criarReserva(Long id, EstadoReserva estado) {
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setEstado(estado);
        return reserva;
    }

    private Reserva criarReservaComAlojamento() {
        Reserva reserva = criarReserva(10L, EstadoReserva.CONCLUIDA);
        Alojamento alojamento = new Alojamento();
        try {
            var field = Alojamento.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(alojamento, 1L);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        reserva.setAlojamento(alojamento);
        return reserva;
    }

    private Estadia criarEstadia(Long id, EstadoEstadia estado) {
        Estadia estadia = new Estadia();
        estadia.setId(id);
        estadia.setEstado(estado);
        estadia.setDataInicio(LocalDateTime.now().minusDays(1));
        return estadia;
    }
}
