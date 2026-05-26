package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.EstadoReserva;
import pt.hotel.animais.repository.ReservaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceCancelTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ITutorService tutorService;

    @Mock
    private IAnimalService animalService;

    @Mock
    private IAvailabilityDomainService availabilityDomainService;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void cancelarDeveMarcarReservaComoCancelada() {
        assertThat(availabilityDomainService).isNotNull();
        assertThat(tutorService).isNotNull();
        assertThat(animalService).isNotNull();

        Reserva reserva = criarReserva(10L, EstadoReserva.ATIVA);
        when(reservaRepository.findWithDetalhesById(10L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva resultado = reservaService.cancelar(10L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        verify(reservaRepository).save(reserva);
    }

    @Test
    void cancelarDeveRejeitarReservaJaCancelada() {
        assertThat(availabilityDomainService).isNotNull();
        assertThat(tutorService).isNotNull();
        assertThat(animalService).isNotNull();

        Reserva reserva = criarReserva(11L, EstadoReserva.CANCELADA);
        when(reservaRepository.findWithDetalhesById(11L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.cancelar(11L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("não pode ser cancelada");

        verify(reservaRepository, never()).save(any());
    }

    private Reserva criarReserva(Long id, EstadoReserva estado) {
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        tutor.setNome("Tutor Cancel");
        tutor.setNif("222222222");
        tutor.setContacto("912345678");
        tutor.setEmail("cancel@test.local");

        Animal animal = new Animal();
        animal.setId(2L);
        animal.setTutor(tutor);
        animal.setNome("Luna");
        animal.setEspecie(Especie.CAO);
        animal.setRaca("SRD");
        animal.setDataNascimento(LocalDate.now().minusYears(2));
        animal.setPeso(new BigDecimal("10.0"));

        Alojamento alojamento = new Alojamento(3L, "Box Cancel", "CANINO", 1, EstadoLimpeza.CONCLUIDO, null);

        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setTutor(tutor);
        reserva.setAnimal(animal);
        reserva.setAlojamento(alojamento);
        reserva.setDataInicio(LocalDate.now().plusDays(1));
        reserva.setDataFim(LocalDate.now().plusDays(3));
        reserva.setEstado(estado);
        return reserva;
    }
}
