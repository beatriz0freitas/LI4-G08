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
import pt.hotel.animais.service.EstadiaService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CheckOutIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EstadiaService estadiaService;
    @Autowired private TutorRepository tutorRepository;
    @Autowired private AnimalRepository animalRepository;
    @Autowired private AlojamentoRepository alojamentoRepository;
    @Autowired private ReservaRepository reservaRepository;

    @Test
    @WithMockUser(roles = "DIRETOR")
    void postCheckOutComEstadiaEmCurso_redirecionaParaHistoricoComSucesso() throws Exception {
        Estadia estadia = criarEstadiaEmCurso("INT-CO-01");

        mockMvc.perform(post("/estadias/check-out")
                        .param("estadiaId", String.valueOf(estadia.getId()))
                        .param("metodoPagamento", "CARTAO_DEBITO")
                        .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/historico"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void postCheckOutComEstadiaInexistente_redirecionaParaHistoricoComErro() throws Exception {
        mockMvc.perform(post("/estadias/check-out")
                        .param("estadiaId", "99999")
                        .param("metodoPagamento", "NUMERARIO")
                        .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/historico"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void postCheckOutSemMetodoPagamento_redirecionaComErro() throws Exception {
        Estadia estadia = criarEstadiaEmCurso("INT-CO-02");

        mockMvc.perform(post("/estadias/check-out")
                        .param("estadiaId", String.valueOf(estadia.getId()))
                        .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/historico"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void getFormComEstadiaIdValido_exibeResumoCheckOut() throws Exception {
        criarEstadiaEmCurso("INT-CO-03");

        mockMvc.perform(get("/estadias/lista"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("estadias"));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Estadia criarEstadiaEmCurso(String nifSuffix) {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Integration CO");
        tutor.setNif("NIF-" + nifSuffix);
        tutor.setContacto("920000001");
        tutor.setEmail("co-int-" + nifSuffix + "@teste.pt");
        tutorRepository.saveAndFlush(tutor);

        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome("Animal Int " + nifSuffix);
        animal.setEspecie(Especie.CAO);
        animal.setRaca("Boxer");
        animal.setDataNascimento(LocalDate.of(2018, 5, 20));
        animal.setPeso(new BigDecimal("25.00"));
        animal.setEstadoSaude(EstadoSaude.NORMAL);
        animalRepository.saveAndFlush(animal);

        Alojamento alojamento = alojamentoRepository.findAllByOrderByIdentificacaoAsc().get(0);

        Reserva reserva = new Reserva();
        reserva.setTutor(tutor);
        reserva.setAnimal(animal);
        reserva.setAlojamento(alojamento);
        reserva.setDataInicio(LocalDate.now().minusDays(1));
        reserva.setDataFim(LocalDate.now().plusDays(2));
        reserva.setEstado(EstadoReserva.ATIVA);
        reservaRepository.saveAndFlush(reserva);

        return estadiaService.abrirEstadiaPorReserva(reserva.getId(), MetodoPagamento.NUMERARIO);
    }
}
