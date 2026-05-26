package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityDomainServiceTest {

    @Mock
    private AlojamentoRepository alojamentoRepository;

    @InjectMocks
    private AvailabilityDomainService service;

    @Test
    void estaDisponivelDeveAceitarAlojamentoLivreLimpoEAdequado() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fim = inicio.plusDays(2);
        Alojamento alojamento = alojamento(1L, "CANINO", EstadoLimpeza.CONCLUIDO);

        when(alojamentoRepository.findById(1L)).thenReturn(Optional.of(alojamento));
        when(alojamentoRepository.countConflictingReservas(1L, inicio, fim)).thenReturn(0L);
        when(alojamentoRepository.countEstadiasAtivasPorAlojamento(1L)).thenReturn(0L);

        boolean resultado = service.estaDisponivel(1L, inicio, fim, Especie.CAO);

        assertThat(resultado).isTrue();
    }

    @Test
    void estaDisponivelDeveRejeitarReservaSobreposta() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fim = inicio.plusDays(2);
        Alojamento alojamento = alojamento(1L, "CANINO", EstadoLimpeza.CONCLUIDO);

        when(alojamentoRepository.findById(1L)).thenReturn(Optional.of(alojamento));
        when(alojamentoRepository.countConflictingReservas(1L, inicio, fim)).thenReturn(1L);

        boolean resultado = service.estaDisponivel(1L, inicio, fim, Especie.CAO);

        assertThat(resultado).isFalse();
        verify(alojamentoRepository, never()).countEstadiasAtivasPorAlojamento(1L);
    }

    @Test
    void estaDisponivelDeveRejeitarEstadiaAtiva() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fim = inicio.plusDays(2);
        Alojamento alojamento = alojamento(1L, "CANINO", EstadoLimpeza.CONCLUIDO);

        when(alojamentoRepository.findById(1L)).thenReturn(Optional.of(alojamento));
        when(alojamentoRepository.countConflictingReservas(1L, inicio, fim)).thenReturn(0L);
        when(alojamentoRepository.countEstadiasAtivasPorAlojamento(1L)).thenReturn(1L);

        boolean resultado = service.estaDisponivel(1L, inicio, fim, Especie.CAO);

        assertThat(resultado).isFalse();
    }

    @Test
    void estaDisponivelDeveRejeitarLimpezaPendente() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fim = inicio.plusDays(2);
        Alojamento alojamento = alojamento(1L, "CANINO", EstadoLimpeza.PENDENTE);

        when(alojamentoRepository.findById(1L)).thenReturn(Optional.of(alojamento));

        boolean resultado = service.estaDisponivel(1L, inicio, fim, Especie.CAO);

        assertThat(resultado).isFalse();
        verify(alojamentoRepository, never()).countConflictingReservas(1L, inicio, fim);
    }

    @Test
    void validarDisponivelParaReservaComLockDeveBloquearAlojamentoIndisponivel() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fim = inicio.plusDays(2);
        Alojamento alojamento = alojamento(1L, "CANINO", EstadoLimpeza.CONCLUIDO);

        when(alojamentoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(alojamento));
        when(alojamentoRepository.countConflictingReservas(1L, inicio, fim)).thenReturn(0L);
        when(alojamentoRepository.countEstadiasAtivasPorAlojamento(1L)).thenReturn(1L);

        assertThatThrownBy(() -> service.validarDisponivelParaReservaComLock(1L, inicio, fim, Especie.CAO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("não está disponível");
    }

    private Alojamento alojamento(Long id, String tipo, EstadoLimpeza estadoLimpeza) {
        return new Alojamento(id, "Box " + id, tipo, 2, estadoLimpeza, null);
    }
}
