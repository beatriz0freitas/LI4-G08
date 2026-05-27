package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.*;
import pt.hotel.animais.model.enums.*;
import pt.hotel.animais.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CheckInServiceTest {

    @Autowired private EstadiaService estadiaService;
    @Autowired private EstadiaRepository estadiaRepository;
    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private TutorRepository tutorRepository;
    @Autowired private AnimalRepository animalRepository;
    @Autowired private AlojamentoRepository alojamentoRepository;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private PlanoCuidadosRepository planoCuidadosRepository;

    @Test
    void checkInCriaEstadiaEmCursoERegistaPagamentoCheckIn() {
        Reserva reserva = criarReserva("CI-001");

        Estadia estadia = estadiaService.abrirEstadiaPorReserva(reserva.getId(), MetodoPagamento.NUMERARIO);

        assertThat(estadia.getId()).isNotNull();
        assertThat(estadia.getEstado()).isEqualTo(EstadoEstadia.EM_CURSO);
        assertThat(estadia.getDataInicio()).isNotNull();
        assertThat(estadia.getReserva().getId()).isEqualTo(reserva.getId());
        PlanoCuidados plano = planoCuidadosRepository.findByEstadiaId(estadia.getId()).orElseThrow();
        assertThat(plano.getAnimal().getId()).isEqualTo(reserva.getAnimal().getId());
        assertThat(plano.getAtivo()).isTrue();

        var pagamentos = pagamentoRepository.findAll();
        assertThat(pagamentos).isNotEmpty();
        assertThat(pagamentos.get(0).getMomentoPagamento()).isEqualTo(MomentoPagamento.CHECK_IN);
        assertThat(pagamentos.get(0).getValor()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void checkInAtualizaEstadoReservaParaConfirmada() {
        Reserva reserva = criarReserva("CI-002");

        estadiaService.abrirEstadiaPorReserva(reserva.getId(), MetodoPagamento.CARTAO_CREDITO);

        Reserva atualizada = reservaRepository.findById(reserva.getId()).orElseThrow();
        assertThat(atualizada.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
    }

    @Test
    void checkInComMetodoNuloLancaExcecaoAntesDeTocarNoDB() {
        Reserva reserva = criarReserva("CI-003");

        assertThatThrownBy(() -> estadiaService.abrirEstadiaPorReserva(reserva.getId(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatório");

        assertThat(estadiaRepository.findAll()).isEmpty();
    }

    @Test
    void checkInComReservaInexistenteLancaExcecao() {
        assertThatThrownBy(() -> estadiaService.abrirEstadiaPorReserva(99999L, MetodoPagamento.NUMERARIO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void checkInDuplicadoParaMesmoAnimalLancaExcecao() {
        Reserva reserva1 = criarReserva("CI-004");
        estadiaService.abrirEstadiaPorReserva(reserva1.getId(), MetodoPagamento.NUMERARIO);

        Alojamento outro = alojamentoRepository.findAllByOrderByIdentificacaoAsc()
                .stream().filter(a -> !a.getId().equals(reserva1.getAlojamento().getId()))
                .findFirst().orElseThrow();
        Animal animal = reserva1.getAnimal();
        Reserva reserva2 = criarReservaParaAnimalEAlojamento(animal, outro);

        assertThatThrownBy(() -> estadiaService.abrirEstadiaPorReserva(reserva2.getId(), MetodoPagamento.NUMERARIO))
                .isInstanceOf(RuntimeException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Reserva criarReserva(String nifSuffix) {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Teste CheckIn");
        tutor.setNif("NIF-" + nifSuffix);
        tutor.setContacto("910000000");
        tutor.setEmail("ci-" + nifSuffix + "@teste.pt");
        tutorRepository.saveAndFlush(tutor);

        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome("Animal " + nifSuffix);
        animal.setEspecie(Especie.CAO);
        animal.setRaca("Labrador");
        animal.setDataNascimento(LocalDate.of(2020, 1, 1));
        animal.setPeso(new BigDecimal("10.00"));
        animal.setEstadoSaude(EstadoSaude.NORMAL);
        animalRepository.saveAndFlush(animal);

        Alojamento alojamento = alojamentoRepository.findAllByOrderByIdentificacaoAsc().get(0);
        return criarReservaParaAnimalEAlojamento(animal, alojamento);
    }

    private Reserva criarReservaParaAnimalEAlojamento(Animal animal, Alojamento alojamento) {
        Reserva reserva = new Reserva();
        reserva.setTutor(animal.getTutor());
        reserva.setAnimal(animal);
        reserva.setAlojamento(alojamento);
        reserva.setDataInicio(LocalDate.now());
        reserva.setDataFim(LocalDate.now().plusDays(3));
        reserva.setEstado(EstadoReserva.ATIVA);
        return reservaRepository.saveAndFlush(reserva);
    }
}
