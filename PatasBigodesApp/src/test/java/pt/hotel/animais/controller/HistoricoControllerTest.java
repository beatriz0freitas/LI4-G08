package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.config.SecurityConfig;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.service.IHistoricoService;

import java.time.LocalDate;
import java.util.List;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Tutor;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HistoricoController.class)
@Import(SecurityConfig.class)
class HistoricoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IHistoricoService historicoService;

    @Test
    @WithMockUser(roles = {"DIRETOR"})
    void verEventosDeveRenderizarHistoricoConsolidado() throws Exception {
        when(historicoService.consultar(any(), any()))
            .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        mvc.perform(get("/historico/eventos")
                .param("clienteId", "2")
                .param("animalId", "5")
                .param("estadiaId", "7")
                .param("tipoEvento", "SERVICO_EXTRA")
                .param("dataInicio", LocalDate.now().minusDays(1).toString())
                .param("dataFim", LocalDate.now().toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("historico/eventos"))
            .andExpect(model().attribute("clienteId", 2L))
            .andExpect(model().attribute("animalId", 5L))
            .andExpect(model().attribute("estadiaId", 7L))
            .andExpect(model().attribute("tipoEvento", "SERVICO_EXTRA"));
    }

    @Test
    @WithMockUser(roles = {"DIRETOR"})
    void verHistoricoDeveRenderizarListaDeEstadias() throws Exception {
        Estadia estadia = new Estadia();
        estadia.setId(3L);
        estadia.setEstado(EstadoEstadia.EM_CURSO);
        Reserva reserva = new Reserva();
        reserva.setId(42L);
        Tutor tutor = new Tutor();
        tutor.setNome("Maria Silva");
        Animal animal = new Animal();
        animal.setNome("Fido");
        animal.setTutor(tutor);
        Alojamento alojamento = new Alojamento();
        alojamento.setIdentificacao("A1");
        reserva.setAlojamento(alojamento);
        reserva.setTutor(tutor);
        reserva.setAnimal(animal);
        estadia.setReserva(reserva);

        when(historicoService.listarHistorico(eq(2L), eq(5L), eq(EstadoEstadia.EM_CURSO), any(), any(), any()))
            .thenReturn(new PageImpl<>(List.of(estadia), PageRequest.of(0, 10), 12));

        mvc.perform(get("/historico")
                .param("clienteId", "2")
                .param("animalId", "5")
                .param("estado", "EM_CURSO"))
            .andExpect(status().isOk())
            .andExpect(view().name("historico/list"))
            .andExpect(model().attribute("clienteId", 2L))
            .andExpect(model().attribute("animalId", 5L))
            .andExpect(content().string(containsString("/estadias?estadiaId=3")))
            .andExpect(content().string(containsString("Preparar Check-out")))
            .andExpect(content().string(containsString("/historico?page=1")))
            .andExpect(content().string(not(containsString("action=\"/estadias/check-out\""))));
    }
}
