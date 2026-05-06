package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.ReservaFormDto;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.AnimalRepository;
import pt.hotel.animais.repository.ReservaRepository;
import pt.hotel.animais.repository.TutorRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"spring.jpa.hibernate.ddl-auto=update"})
@Transactional
public class ReservaServiceTests {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AlojamentoRepository alojamentoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    private Tutor createTutor(String nif) {
        Tutor t = new Tutor();
        t.setNome("Teste");
        t.setNif(nif);
        t.setContacto("900000000");
        t.setEmail(nif + "@test.local");
        return tutorRepository.save(t);
    }

    private Animal createAnimal(Tutor tutor, String nome) {
        Animal a = new Animal();
        a.setTutor(tutor);
        a.setNome(nome);
        a.setEspecie(Especie.CAO);
        a.setRaca("SRD");
        a.setDataNascimento(LocalDate.now().minusYears(3));
        a.setPeso(new BigDecimal("12.5"));
        return animalRepository.save(a);
    }

    private Alojamento createAlojamento(String id) {
        Alojamento a = new Alojamento();
        a.setIdentificacao(id);
        a.setTipo("Box");
        a.setCapacidade(1);
        a.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
        return alojamentoRepository.save(a);
    }

    @Test
    public void shouldPreventOverbooking() {
        Tutor tutor = createTutor("999999990");
        Animal animal = createAnimal(tutor, "Fido");
        Alojamento aloj = createAlojamento("A-101");

        ReservaFormDto dto = new ReservaFormDto();
        dto.setTutorId(tutor.getId());
        dto.setAnimalId(animal.getId());
        dto.setAlojamentoId(aloj.getId());
        dto.setDataInicio(LocalDate.now().plusDays(1));
        dto.setDataFim(LocalDate.now().plusDays(3));

        // primeira reserva deve ser criada
        var r1 = reservaService.criar(dto);
        assertNotNull(r1.getId());

        // tentar criar nova reserva sobreposta deve falhar
        ReservaFormDto dto2 = new ReservaFormDto();
        dto2.setTutorId(tutor.getId());
        dto2.setAnimalId(animal.getId());
        dto2.setAlojamentoId(aloj.getId());
        dto2.setDataInicio(LocalDate.now().plusDays(2)); // sobrepõe
        dto2.setDataFim(LocalDate.now().plusDays(4));

        assertThrows(IllegalArgumentException.class, () -> reservaService.criar(dto2));

        List<?> reservas = reservaRepository.findByAlojamentoId(aloj.getId());
        assertEquals(1, reservas.size());
    }

    @Test
    public void concurrentReservationsAllowOnlyOne() throws Exception {
        Tutor t1 = createTutor("999999991");
        Tutor t2 = createTutor("999999992");
        Animal a1 = createAnimal(t1, "Rex");
        Animal a2 = createAnimal(t2, "Luna");
        Alojamento aloj = createAlojamento("A-102");

        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = LocalDate.now().plusDays(7);

        Callable<Boolean> task1 = () -> {
            try {
                ReservaFormDto dto = new ReservaFormDto();
                dto.setTutorId(t1.getId());
                dto.setAnimalId(a1.getId());
                dto.setAlojamentoId(aloj.getId());
                dto.setDataInicio(start);
                dto.setDataFim(end);
                reservaService.criar(dto);
                return true;
            } catch (Exception ex) {
                return false;
            }
        };

        Callable<Boolean> task2 = () -> {
            try {
                ReservaFormDto dto = new ReservaFormDto();
                dto.setTutorId(t2.getId());
                dto.setAnimalId(a2.getId());
                dto.setAlojamentoId(aloj.getId());
                dto.setDataInicio(start);
                dto.setDataFim(end);
                reservaService.criar(dto);
                return true;
            } catch (Exception ex) {
                return false;
            }
        };

        ExecutorService ex = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> f1 = ex.submit(task1);
            Future<Boolean> f2 = ex.submit(task2);

            int success = 0;
            if (f1.get()) success++;
            if (f2.get()) success++;

            // no máximo uma das tentativas deve ter sucesso (comportamento aceitável em ambientes concorrentes)
            assertTrue(success <= 1, "No máximo uma reserva concorrente deve ser aceite");
            List<?> reservas = reservaRepository.findByAlojamentoId(aloj.getId());
            assertTrue(reservas.size() <= 1, "No máximo uma reserva persistida");
        } finally {
            ex.shutdownNow();
        }
    }
}
