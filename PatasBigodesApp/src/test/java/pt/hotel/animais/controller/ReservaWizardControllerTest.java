package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReservaWizardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AlojamentoRepository alojamentoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Test
    @WithMockUser(username = "recepcao", roles = {"FUNCIONARIO_RECEPCAO"})
    void assistenteDeNovaReservaDeveAvancarAteCriarReserva() throws Exception {
        Tutor tutor = criarTutor("288888888");
        Animal animal = criarAnimal(tutor);
        Alojamento alojamento = criarAlojamento("Wizard-CAN-1");
        LocalDate dataInicio = LocalDate.now().plusDays(15);
        LocalDate dataFim = dataInicio.plusDays(3);

        mockMvc.perform(get("/reservas/novo"))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/form"))
            .andExpect(model().attributeDoesNotExist("activeStep"));

        mockMvc.perform(get("/reservas/novo")
                .param("tutorId", tutor.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/form"))
            .andExpect(model().attributeDoesNotExist("activeStep"))
            .andExpect(model().attribute("animaisTutor", hasItem(hasProperty("id", is(animal.getId())))));

        mockMvc.perform(get("/reservas/novo")
                .param("tutorId", tutor.getId().toString())
                .param("animalId", animal.getId().toString())
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/form"))
            .andExpect(model().attributeDoesNotExist("activeStep"))
            .andExpect(model().attribute("disponibilidades", not(empty())))
            .andExpect(content().string(containsString("value=\"" + dataInicio + "\"")))
            .andExpect(content().string(containsString("value=\"" + dataFim + "\"")));

        mockMvc.perform(get("/reservas/novo")
                .param("tutorId", tutor.getId().toString())
                .param("animalId", animal.getId().toString())
                .param("alojamentoId", alojamento.getId().toString())
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/form"))
            .andExpect(model().attributeDoesNotExist("activeStep"))
            .andExpect(content().string(containsString("value=\"" + dataInicio + "\"")))
            .andExpect(content().string(containsString("value=\"" + dataFim + "\"")));

        long reservasAntes = reservaRepository.count();

        mockMvc.perform(post("/reservas")
                .with(csrf())
                .param("tutorId", tutor.getId().toString())
                .param("animalId", animal.getId().toString())
                .param("alojamentoId", alojamento.getId().toString())
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/reservas/*"));

        org.junit.jupiter.api.Assertions.assertEquals(reservasAntes + 1, reservaRepository.count());
    }

    @Test
    @WithMockUser(username = "recepcao", roles = {"FUNCIONARIO_RECEPCAO"})
    void assistenteDeveRecuarQuandoParametrosFicamInvalidos() throws Exception {
        Tutor tutor = criarTutor("277777777");

        mockMvc.perform(get("/reservas/novo")
                .param("tutorId", tutor.getId().toString())
                .param("animalId", "999999"))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/form"))
            .andExpect(model().attributeDoesNotExist("activeStep"))
            .andExpect(model().attribute("reservaForm", hasProperty("animalId", nullValue())));

        mockMvc.perform(get("/reservas/novo")
                .param("step", "passo-inexistente"))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/form"))
            .andExpect(model().attributeDoesNotExist("activeStep"));
    }

    private Tutor criarTutor(String nif) {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Reserva");
        tutor.setNif(nif);
        tutor.setContacto("912345678");
        tutor.setEmail(nif + "@test.local");
        return tutorRepository.save(tutor);
    }

    private Animal criarAnimal(Tutor tutor) {
        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome("Rex");
        animal.setEspecie(Especie.CAO);
        animal.setRaca("Serra da Estrela");
        animal.setDataNascimento(LocalDate.now().minusYears(4));
        animal.setPeso(new BigDecimal("18.50"));
        return animalRepository.save(animal);
    }

    private Alojamento criarAlojamento(String identificacao) {
        Alojamento alojamento = new Alojamento();
        alojamento.setIdentificacao(identificacao);
        alojamento.setTipo("CANINO");
        alojamento.setCapacidade(1);
        alojamento.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
        return alojamentoRepository.save(alojamento);
    }
}
