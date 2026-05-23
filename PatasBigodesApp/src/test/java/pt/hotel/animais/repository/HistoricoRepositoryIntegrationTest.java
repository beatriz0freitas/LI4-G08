package pt.hotel.animais.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.EstadoReserva;
import pt.hotel.animais.model.enums.TipoAlojamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HistoricoRepositoryIntegrationTest {

    @Autowired
    private EstadiaRepository estadiaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AlojamentoRepository alojamentoRepository;

    @Test
    void pesquisarHistorico_respeita_filtros_e_paginacao() {
        Tutor tutor1 = criarTutor("Tutor 1", "111111111", "900000001", "t1@exemplo.com");
        Tutor tutor2 = criarTutor("Tutor 2", "222222222", "900000002", "t2@exemplo.com");

        Animal animal1 = criarAnimal(tutor1, "Rex");
        Animal animal2 = criarAnimal(tutor1, "Mia");
        Animal animal3 = criarAnimal(tutor2, "Luna");

        Alojamento alojamento1 = criarAlojamento("A-01");
        Alojamento alojamento2 = criarAlojamento("A-02");
        Alojamento alojamento3 = criarAlojamento("A-03");

        criarEstadia(tutor1, animal1, alojamento1, LocalDate.now().minusDays(20), LocalDate.now().minusDays(18), EstadoEstadia.EM_CURSO);
        criarEstadia(tutor1, animal2, alojamento2, LocalDate.now().minusDays(10), LocalDate.now().minusDays(8), EstadoEstadia.TERMINADA);
        criarEstadia(tutor2, animal3, alojamento3, LocalDate.now().minusDays(3), LocalDate.now().minusDays(1), EstadoEstadia.TERMINADA);

        Page<Estadia> pagina = estadiaRepository.pesquisarHistorico(
            tutor1.getId(),
            null,
            null,
            null,
            null,
            PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "dataCriacao"))
        );

        Assertions.assertEquals(2, pagina.getTotalElements());
        Assertions.assertEquals(2, pagina.getTotalPages());
        Assertions.assertEquals(1, pagina.getContent().size());
        Assertions.assertEquals(tutor1.getId(), pagina.getContent().get(0).getReserva().getTutor().getId());

        Page<Estadia> filtrada = estadiaRepository.pesquisarHistorico(
            tutor1.getId(),
            animal2.getId(),
            EstadoEstadia.TERMINADA,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.now().plusDays(1),
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataCriacao"))
        );

        Assertions.assertEquals(1, filtrada.getTotalElements());
        Assertions.assertEquals(EstadoEstadia.TERMINADA, filtrada.getContent().get(0).getEstado());
        Assertions.assertEquals(animal2.getId(), filtrada.getContent().get(0).getReserva().getAnimal().getId());
    }

    private Tutor criarTutor(String nome, String nif, String contacto, String email) {
        Tutor tutor = new Tutor();
        tutor.setNome(nome);
        tutor.setNif(nif);
        tutor.setContacto(contacto);
        tutor.setEmail(email);
        return tutorRepository.save(tutor);
    }

    private Animal criarAnimal(Tutor tutor, String nome) {
        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome(nome);
        animal.setEspecie(Especie.CAO);
        animal.setRaca("SRD");
        animal.setDataNascimento(LocalDate.now().minusYears(3));
        animal.setPeso(new BigDecimal("12.5"));
        return animalRepository.save(animal);
    }

    private Alojamento criarAlojamento(String identificacao) {
        Alojamento alojamento = new Alojamento();
        alojamento.setIdentificacao(identificacao);
        alojamento.setCapacidade(2);
        alojamento.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
        alojamento.setTipo(TipoAlojamento.CANINO);
        return alojamentoRepository.save(alojamento);
    }

    private Estadia criarEstadia(Tutor tutor, Animal animal, Alojamento alojamento, LocalDate dataInicio, LocalDate dataFim, EstadoEstadia estado) {
        Reserva reserva = new Reserva();
        reserva.setTutor(tutor);
        reserva.setAnimal(animal);
        reserva.setAlojamento(alojamento);
        reserva.setDataInicio(dataInicio);
        reserva.setDataFim(dataFim);
        reserva.setEstado(EstadoReserva.CONCLUIDA);
        reserva = reservaRepository.save(reserva);

        Estadia estadia = new Estadia();
        estadia.setReserva(reserva);
        estadia.setDataInicio(dataInicio.atStartOfDay());
        estadia.setDataFim(dataFim.atTime(23, 59, 59));
        estadia.setEstado(estado);
        return estadiaRepository.save(estadia);
    }
}
