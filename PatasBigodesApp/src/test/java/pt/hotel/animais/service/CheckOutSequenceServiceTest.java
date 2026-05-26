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
class CheckOutSequenceServiceTest {

    @Autowired private EstadiaService estadiaService;
    @Autowired private EstadiaRepository estadiaRepository;
    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private TutorRepository tutorRepository;
    @Autowired private AnimalRepository animalRepository;
    @Autowired private AlojamentoRepository alojamentoRepository;
    @Autowired private ReservaRepository reservaRepository;

    @Test
    void sequenciaCheckInCheckOutTerminaEstadiaERegistaDoisPagamentos() {
        Reserva reserva = criarReserva("CO-001");
        Estadia estadia = estadiaService.abrirEstadiaPorReserva(reserva.getId(), MetodoPagamento.NUMERARIO);

        Estadia terminada = estadiaService.checkOut(estadia.getId(), MetodoPagamento.NUMERARIO);

        assertThat(terminada.getEstado()).isEqualTo(EstadoEstadia.TERMINADA);
        assertThat(terminada.getDataFim()).isNotNull();

        var pagamentos = pagamentoRepository.findAll();
        assertThat(pagamentos).hasSize(2);
        assertThat(pagamentos).anyMatch(p -> p.getMomentoPagamento() == MomentoPagamento.CHECK_IN);
        assertThat(pagamentos).anyMatch(p -> p.getMomentoPagamento() == MomentoPagamento.CHECK_OUT);
    }

    @Test
    void checkOutAtualizaReservaParaConcluida() {
        Reserva reserva = criarReserva("CO-002");
        Estadia estadia = estadiaService.abrirEstadiaPorReserva(reserva.getId(), MetodoPagamento.CARTAO_DEBITO);

        estadiaService.checkOut(estadia.getId(), MetodoPagamento.CARTAO_DEBITO);

        Reserva atualizada = reservaRepository.findById(reserva.getId()).orElseThrow();
        assertThat(atualizada.getEstado()).isEqualTo(EstadoReserva.CONCLUIDA);
    }

    @Test
    void checkOutComMetodoNuloLancaExcecao() {
        Reserva reserva = criarReserva("CO-003");
        Estadia estadia = estadiaService.abrirEstadiaPorReserva(reserva.getId(), MetodoPagamento.NUMERARIO);

        assertThatThrownBy(() -> estadiaService.checkOut(estadia.getId(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatório");
    }

    @Test
    void checkOutComEstadiaInexistenteLancaExcecao() {
        assertThatThrownBy(() -> estadiaService.checkOut(99999L, MetodoPagamento.NUMERARIO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void checkOutEmEstadiaJaTerminadaLancaExcecao() {
        Reserva reserva = criarReserva("CO-004");
        Estadia estadia = estadiaService.abrirEstadiaPorReserva(reserva.getId(), MetodoPagamento.NUMERARIO);
        estadiaService.checkOut(estadia.getId(), MetodoPagamento.NUMERARIO);

        assertThatThrownBy(() -> estadiaService.checkOut(estadia.getId(), MetodoPagamento.NUMERARIO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não está em curso");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Reserva criarReserva(String nifSuffix) {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Teste CheckOut");
        tutor.setNif("NIF-" + nifSuffix);
        tutor.setContacto("920000000");
        tutor.setEmail("co-" + nifSuffix + "@teste.pt");
        tutorRepository.saveAndFlush(tutor);

        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome("Animal " + nifSuffix);
        animal.setEspecie(Especie.CAO);
        animal.setRaca("Beagle");
        animal.setDataNascimento(LocalDate.of(2019, 6, 15));
        animal.setPeso(new BigDecimal("8.00"));
        animal.setEstadoSaude(EstadoSaude.NORMAL);
        animalRepository.saveAndFlush(animal);

        Alojamento alojamento = alojamentoRepository.findAllByOrderByIdentificacaoAsc().get(0);

        Reserva reserva = new Reserva();
        reserva.setTutor(tutor);
        reserva.setAnimal(animal);
        reserva.setAlojamento(alojamento);
        reserva.setDataInicio(LocalDate.now().minusDays(2));
        reserva.setDataFim(LocalDate.now().plusDays(1));
        reserva.setEstado(EstadoReserva.ATIVA);
        return reservaRepository.saveAndFlush(reserva);
    }
}
