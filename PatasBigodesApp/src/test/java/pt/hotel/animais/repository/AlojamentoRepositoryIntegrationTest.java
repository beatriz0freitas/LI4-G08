package pt.hotel.animais.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.EstadoReserva;
import pt.hotel.animais.model.enums.Especie;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AlojamentoRepositoryIntegrationTest {

    @Autowired
    private AlojamentoRepository alojamentoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EstadiaRepository estadiaRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Test
    void findAvailableForPeriod_excludes_alojamentos_with_conflicting_reservas() {
        // criar tutor e animal
        Tutor t = new Tutor();
        t.setNome("Teste");
        t.setNif("999999999");
        t.setContacto("900000000");
        t.setEmail("t@t.com");
        tutorRepository.save(t);

        Animal a = new Animal();
        a.setNome("Bicho");
        a.setTutor(t);
        a.setEspecie(Especie.CAO);
        a.setRaca("SRD");
        a.setDataNascimento(LocalDate.now().minusYears(2));
        a.setPeso(new java.math.BigDecimal("10.0"));
        animalRepository.save(a);

        // alojamento A (limpo) com reserva que conflita
        Alojamento alo1 = new Alojamento();
        alo1.setIdentificacao("A1");
        alo1.setCapacidade(2);
        alo1.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
        alo1.setTipo("CANINO");
        alojamentoRepository.save(alo1);

        // alojamento B (limpo) sem reservas
        Alojamento alo2 = new Alojamento();
        alo2.setIdentificacao("B1");
        alo2.setCapacidade(2);
        alo2.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
        alo2.setTipo("CANINO");
        alojamentoRepository.save(alo2);

        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fim = inicio.plusDays(3);

        // criar reserva que ocupa alo1 no período
        Reserva r = new Reserva();
        r.setTutor(t);
        r.setAnimal(a);
        r.setAlojamento(alo1);
        r.setDataInicio(inicio);
        r.setDataFim(fim);
        reservaRepository.save(r);

        List<Alojamento> available = alojamentoRepository.findAvailableForPeriod(inicio, fim);

        Assertions.assertTrue(available.stream().anyMatch(x -> x.getIdentificacao().equals("B1")));
        Assertions.assertTrue(available.stream().noneMatch(x -> x.getIdentificacao().equals("A1")));
    }

    @Test
    void findAvailableForPeriod_excludes_alojamentos_with_active_estadias() {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Estadia");
        tutor.setNif("999999998");
        tutor.setContacto("900000001");
        tutor.setEmail("estadia@t.com");
        tutorRepository.save(tutor);

        Animal animal = new Animal();
        animal.setNome("Animal Estadia");
        animal.setTutor(tutor);
        animal.setEspecie(Especie.CAO);
        animal.setRaca("SRD");
        animal.setDataNascimento(LocalDate.now().minusYears(2));
        animal.setPeso(new java.math.BigDecimal("10.0"));
        animalRepository.save(animal);

        Alojamento ocupado = new Alojamento();
        ocupado.setIdentificacao("O1");
        ocupado.setCapacidade(2);
        ocupado.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
        ocupado.setTipo("CANINO");
        alojamentoRepository.save(ocupado);

        Alojamento livre = new Alojamento();
        livre.setIdentificacao("L1");
        livre.setCapacidade(2);
        livre.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
        livre.setTipo("CANINO");
        alojamentoRepository.save(livre);

        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fim = inicio.plusDays(3);

        Reserva reserva = new Reserva();
        reserva.setTutor(tutor);
        reserva.setAnimal(animal);
        reserva.setAlojamento(ocupado);
        reserva.setDataInicio(LocalDate.now().minusDays(1));
        reserva.setDataFim(LocalDate.now().plusDays(1));
        reserva.setEstado(EstadoReserva.CONCLUIDA);
        reservaRepository.save(reserva);

        Estadia estadia = new Estadia();
        estadia.setReserva(reserva);
        estadia.setDataInicio(LocalDateTime.now().minusDays(1));
        estadia.setEstado(EstadoEstadia.EM_CURSO);
        estadiaRepository.save(estadia);

        List<Alojamento> disponiveis = alojamentoRepository.findAvailableForPeriod(inicio, fim);

        Assertions.assertTrue(disponiveis.stream().anyMatch(x -> x.getIdentificacao().equals("L1")));
        Assertions.assertTrue(disponiveis.stream().noneMatch(x -> x.getIdentificacao().equals("O1")));
    }
}
