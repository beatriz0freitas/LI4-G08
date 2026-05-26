package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.dto.ReservaFormDto;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.EstadoReserva;
import pt.hotel.animais.repository.ReservaRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceCreateTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ITutorService tutorService;

    @Mock
    private IAnimalService animalService;

    @Mock
    private IAvailabilityDomainService availabilityDomainService;

    @Mock
    private AuditoriaOperacaoService auditoriaOperacaoService;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void criarDevePersistirReservaAtivaQuandoDadosValidos() {
        Tutor tutor = criarTutor(1L);
        Animal animal = criarAnimal(2L, tutor);
        Alojamento alojamento = criarAlojamento(3L);
        ReservaFormDto form = criarForm(1L, 2L, 3L);

        when(tutorService.obter(1L)).thenReturn(tutor);
        when(animalService.obter(2L)).thenReturn(animal);
        when(reservaRepository.countActiveInPeriod(3L, form.getDataInicio(), form.getDataFim())).thenReturn(0L);
        when(availabilityDomainService.validarDisponivelParaReservaComLock(
            3L,
            form.getDataInicio(),
            form.getDataFim(),
            Especie.CAO
        )).thenReturn(alojamento);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva reserva = invocation.getArgument(0);
            reserva.setId(99L);
            return reserva;
        });

        Reserva resultado = reservaService.criar(form);

        assertThat(resultado.getId()).isEqualTo(99L);
        assertThat(resultado.getEstado()).isEqualTo(EstadoReserva.ATIVA);
        assertThat(resultado.getTutor().getId()).isEqualTo(1L);
        assertThat(resultado.getAnimal().getId()).isEqualTo(2L);
        assertThat(resultado.getAlojamento()).isSameAs(alojamento);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void criarDeveRejeitarAnimalQueNaoPertenceAoTutor() {
        Tutor tutor = criarTutor(1L);
        Tutor tutorDiferente = criarTutor(2L);
        Animal animal = criarAnimal(2L, tutorDiferente);
        Alojamento alojamento = criarAlojamento(3L);
        ReservaFormDto form = criarForm(1L, 2L, 3L);

        when(tutorService.obter(1L)).thenReturn(tutor);
        when(animalService.obter(2L)).thenReturn(animal);
        

        assertThatThrownBy(() -> reservaService.criar(form))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("não pertence ao tutor especificado");

        verify(reservaRepository, never()).save(any());
    }

    private ReservaFormDto criarForm(Long tutorId, Long animalId, Long alojamentoId) {
        ReservaFormDto form = new ReservaFormDto();
        form.setTutorId(tutorId);
        form.setAnimalId(animalId);
        form.setAlojamentoId(alojamentoId);
        form.setDataInicio(LocalDate.now().plusDays(10));
        form.setDataFim(LocalDate.now().plusDays(12));
        return form;
    }

    private Tutor criarTutor(Long id) {
        Tutor tutor = new Tutor();
        tutor.setId(id);
        tutor.setNome("Tutor " + id);
        tutor.setNif("20000000" + id);
        tutor.setContacto("91234567" + id);
        tutor.setEmail("tutor" + id + "@test.local");
        return tutor;
    }

    private Animal criarAnimal(Long id, Tutor tutor) {
        Animal animal = new Animal();
        animal.setId(id);
        animal.setTutor(tutor);
        animal.setNome("Animal " + id);
        animal.setEspecie(Especie.CAO);
        animal.setRaca("SRD");
        animal.setDataNascimento(LocalDate.now().minusYears(3));
        animal.setPeso(new BigDecimal("12.5"));
        return animal;
    }

    private Alojamento criarAlojamento(Long id) {
        Alojamento alojamento = new Alojamento();
        definirCampo(alojamento, "id", id);
        definirCampo(alojamento, "identificacao", "Box " + id);
        definirCampo(alojamento, "tipo", "CANINO");
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
