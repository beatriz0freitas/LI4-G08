package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.dto.NotaDto;
import pt.hotel.animais.dto.NotaFormDto;
import pt.hotel.animais.model.Nota;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.repository.NotaRepository;
import pt.hotel.animais.repository.ReservaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotaServiceTest {

    @Mock
    private NotaRepository notaRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private NotaService notaService;

    @Test
    void createDevePersistirNotaAssociadaAReserva() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);

        NotaFormDto form = new NotaFormDto();
        form.setReservaId(1L);
        form.setDescricao("Animal comeu bem hoje");
        form.setDataHora(LocalDateTime.now());

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(notaRepository.save(any(Nota.class))).thenAnswer(inv -> {
            Nota n = inv.getArgument(0);
            n.setId(10L);
            return n;
        });

        NotaDto resultado = notaService.create(form, 5L);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getReservaId()).isEqualTo(1L);
        assertThat(resultado.getDescricao()).isEqualTo("Animal comeu bem hoje");
        assertThat(resultado.getDataHora()).isNotNull();
        verify(notaRepository).save(any(Nota.class));
    }

    @Test
    void createDeveLancarExcecaoSeReservaNaoExistir() {
        NotaFormDto form = new NotaFormDto();
        form.setReservaId(99L);
        form.setDescricao("Nota");
        form.setDataHora(LocalDateTime.now());

        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notaService.create(form, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reserva");

        verify(notaRepository, never()).save(any());
    }

    @Test
    void createDeveAssociarAutorCorrectamente() {
        Reserva reserva = new Reserva();
        reserva.setId(2L);
        NotaFormDto form = new NotaFormDto();
        form.setReservaId(2L);
        form.setDescricao("Nota de saúde");
        form.setDataHora(LocalDateTime.now());

        when(reservaRepository.findById(2L)).thenReturn(Optional.of(reserva));
        when(notaRepository.save(any(Nota.class))).thenAnswer(inv -> {
            Nota n = inv.getArgument(0);
            n.setId(20L);
            return n;
        });

        NotaDto resultado = notaService.create(form, 7L);

        assertThat(resultado.getId()).isEqualTo(20L);
        verify(notaRepository).save(argThat(n -> Long.valueOf(7L).equals(n.getAutorId())));
    }
}
