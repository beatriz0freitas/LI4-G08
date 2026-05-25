package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.TipoAlojamento;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.AnimalRepository;
import pt.hotel.animais.repository.ReservaRepository;
import pt.hotel.animais.repository.TutorRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ReservaRenderingControllerTest {

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
    void listaEDetalheDevemRenderizarReservaCriadaSemLazyInitializationException() throws Exception {
        Tutor tutor = criarTutor();
        Animal animal = criarAnimal(tutor);
        Alojamento alojamento = criarAlojamento();
        LocalDate dataInicio = LocalDate.now().plusDays(20);
        LocalDate dataFim = dataInicio.plusDays(2);

        mockMvc.perform(post("/reservas")
                .with(csrf())
                .param("tutorId", tutor.getId().toString())
                .param("animalId", animal.getId().toString())
                .param("alojamentoId", alojamento.getId().toString())
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/reservas/*"));

        Long reservaId = reservaRepository.findAll().get(0).getId();

        mockMvc.perform(get("/reservas"))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/index"))
            .andExpect(content().string(containsString("Tutor Lista Reserva")))
            .andExpect(content().string(containsString("Luna Lista Reserva")))
            .andExpect(content().string(containsString("Render-CAN-1")));

        mockMvc.perform(get("/reservas/" + reservaId))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/confirmacao"))
            .andExpect(content().string(containsString("Tutor Lista Reserva")))
            .andExpect(content().string(containsString("Luna Lista Reserva")))
            .andExpect(content().string(containsString("Render-CAN-1")));
    }

    @Test
    @WithMockUser(username = "recepcao", roles = {"FUNCIONARIO_RECEPCAO"})
    void procurarDisponibilidadeDeveRenderizarResultadosELinkParaNovaReserva() throws Exception {
        Alojamento alojamento = criarAlojamento("Procura-CAN-1");
        LocalDate dataInicio = LocalDate.now().plusDays(40);
        LocalDate dataFim = dataInicio.plusDays(3);

        mockMvc.perform(post("/reservas/procurar-disponibilidade")
                .with(csrf())
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("reservas/index"))
            .andExpect(content().string(containsString("Procura-CAN-1")))
            .andExpect(content().string(containsString("alojamentoId=" + alojamento.getId())))
            .andExpect(content().string(containsString("dataInicio=" + dataInicio)))
            .andExpect(content().string(containsString("dataFim=" + dataFim)));
    }

    private Tutor criarTutor() {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Lista Reserva");
        tutor.setNif("266666666");
        tutor.setContacto("912345678");
        tutor.setEmail("lista.reserva@test.local");
        return tutorRepository.save(tutor);
    }

    private Animal criarAnimal(Tutor tutor) {
        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome("Luna Lista Reserva");
        animal.setEspecie(Especie.CAO);
        animal.setRaca("Indefinida");
        animal.setDataNascimento(LocalDate.now().minusYears(3));
        animal.setPeso(new BigDecimal("12.00"));
        return animalRepository.save(animal);
    }

    private Alojamento criarAlojamento() {
        return criarAlojamento("Render-CAN-1");
    }

    private Alojamento criarAlojamento(String identificacao) {
        Alojamento alojamento = new Alojamento();
        alojamento.setIdentificacao(identificacao);
        alojamento.setTipo(TipoAlojamento.CANINO);
        alojamento.setCapacidade(1);
        alojamento.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
        return alojamentoRepository.save(alojamento);
    }
}
