package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoSaude;
import pt.hotel.animais.repository.AnimalRepository;
import pt.hotel.animais.repository.TutorRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TutorAnimalControllerTemplateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Test
    @WithMockUser(username = "recepcao", roles = {"FUNCIONARIO_RECEPCAO"})
    void paginasComEstadoSaudeDevemRenderizar() throws Exception {
        Tutor tutor = new Tutor();
        tutor.setNome("Ana Silva");
        tutor.setNif("299999999");
        tutor.setContacto("912345678");
        tutor.setEmail("ana.silva@test.local");
        tutor = tutorRepository.save(tutor);

        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome("Luna");
        animal.setEspecie(Especie.GATO);
        animal.setRaca("Europeu comum");
        animal.setDataNascimento(LocalDate.now().minusYears(2));
        animal.setPeso(new BigDecimal("4.20"));
        animal.setEstadoSaude(EstadoSaude.ALTERADO);
        animal = animalRepository.save(animal);

        mockMvc.perform(get("/tutores/" + tutor.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("tutores/detail"))
                .andExpect(content().string(containsString("/tutores/" + tutor.getId() + "/animais/novo")))
                .andExpect(content().string(containsString("/reservas/novo?tutorId=" + tutor.getId())))
                .andExpect(content().string(containsString("step=passo2")));

        mockMvc.perform(get("/animais/" + animal.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("animais/detail"))
                .andExpect(content().string(containsString("/tutores/" + tutor.getId())))
                .andExpect(content().string(containsString("/reservas/novo?tutorId=" + tutor.getId())))
                .andExpect(content().string(containsString("animalId=" + animal.getId())))
                .andExpect(content().string(containsString("step=passo3")));

        mockMvc.perform(get("/animais"))
                .andExpect(status().isOk())
                .andExpect(view().name("animais/list"));

        mockMvc.perform(get("/tutores/" + tutor.getId() + "/animais/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("animais/form"));

        mockMvc.perform(get("/reservas/novo")
                        .param("tutorId", tutor.getId().toString())
                        .param("step", "passo2"))
                .andExpect(status().isOk())
                .andExpect(view().name("reservas/form"))
                .andExpect(model().attribute("activeStep", "passo2"));

        mockMvc.perform(get("/reservas/novo")
                        .param("tutorId", tutor.getId().toString())
                        .param("animalId", animal.getId().toString())
                        .param("step", "passo3"))
                .andExpect(status().isOk())
                .andExpect(view().name("reservas/form"))
                .andExpect(model().attribute("activeStep", "passo3"));
    }
}
