package pt.hotel.animais.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.*;
import pt.hotel.animais.model.enums.*;
import pt.hotel.animais.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CheckInIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TutorRepository tutorRepository;
    @Autowired private AnimalRepository animalRepository;
    @Autowired private AlojamentoRepository alojamentoRepository;
    @Autowired private ReservaRepository reservaRepository;

    @Test
    @WithMockUser(roles = "DIRETOR")
    void getFormCheckInCheckOutRetornaOk() throws Exception {
        mockMvc.perform(get("/estadias/lista"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("estadias"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void postCheckInComReservaAtiva_redirecionaParaEstadiasComMensagemSucesso() throws Exception {
        Reserva reserva = criarReserva("INT-CI-01");

        mockMvc.perform(post("/estadias/check-in")
                        .param("reservaId", String.valueOf(reserva.getId()))
                        .param("metodoPagamento", "NUMERARIO")
                        .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/estadias"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void postCheckInComReservaInexistente_redirecionaParaReservasComErro() throws Exception {
        mockMvc.perform(post("/estadias/check-in")
                        .param("reservaId", "99999")
                        .param("metodoPagamento", "NUMERARIO")
                        .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/reservas"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void postCheckInSemMetodoPagamento_redirecionaComErro() throws Exception {
        Reserva reserva = criarReserva("INT-CI-02");

        mockMvc.perform(post("/estadias/check-in")
                        .param("reservaId", String.valueOf(reserva.getId()))
                        .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void getFormComRoleNaoAutorizado_retornaForbidden() throws Exception {
        mockMvc.perform(get("/estadias"))
               .andExpect(status().isForbidden());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Reserva criarReserva(String nifSuffix) {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Integration CI");
        tutor.setNif("NIF-" + nifSuffix);
        tutor.setContacto("910000001");
        tutor.setEmail("ci-int-" + nifSuffix + "@teste.pt");
        tutorRepository.saveAndFlush(tutor);

        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome("Animal Int " + nifSuffix);
        animal.setEspecie(Especie.CAO);
        animal.setRaca("Poodle");
        animal.setDataNascimento(LocalDate.of(2021, 3, 10));
        animal.setPeso(new BigDecimal("5.00"));
        animal.setEstadoSaude(EstadoSaude.NORMAL);
        animalRepository.saveAndFlush(animal);

        Alojamento alojamento = alojamentoRepository.findAllByOrderByIdentificacaoAsc().get(0);

        Reserva reserva = new Reserva();
        reserva.setTutor(tutor);
        reserva.setAnimal(animal);
        reserva.setAlojamento(alojamento);
        reserva.setDataInicio(LocalDate.now());
        reserva.setDataFim(LocalDate.now().plusDays(2));
        reserva.setEstado(EstadoReserva.ATIVA);
        return reservaRepository.saveAndFlush(reserva);
    }
}
