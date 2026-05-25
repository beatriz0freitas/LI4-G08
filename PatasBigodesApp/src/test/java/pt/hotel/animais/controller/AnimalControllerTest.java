package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoSaude;
import pt.hotel.animais.service.IAnimalService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AnimalController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAnimalService animalService;

    @Test
    @WithMockUser
    void listarDeveRenderizarListaAnimais() throws Exception {
        when(animalService.listarTodos()).thenReturn(List.of(criarAnimal(1L, "Rex")));

        mockMvc.perform(get("/animais"))
                .andExpect(status().isOk())
                .andExpect(view().name("animais/list"))
                .andExpect(model().attribute("activePage", "animais"))
                .andExpect(model().attributeExists("animais"));
    }

    @Test
    @WithMockUser
    void detalheDeveRenderizarAnimalExistente() throws Exception {
        Animal a = criarAnimal(1L, "Mimi");
        when(animalService.obter(1L)).thenReturn(a);

        mockMvc.perform(get("/animais/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("animais/detail"))
                .andExpect(model().attributeExists("animal"));
    }

    @Test
    @WithMockUser
    void detalheAnimalInexistenteDeveRedirecionar() throws Exception {
        when(animalService.obter(99L)).thenThrow(new IllegalArgumentException("não encontrado"));

        mockMvc.perform(get("/animais/99"))
                .andExpect(status().is3xxRedirection());
    }

    private Animal criarAnimal(Long id, String nome) {
        Tutor tutor = new Tutor();
        tutor.setId(10L);
        tutor.setNome("Tutor Teste");

        Animal a = new Animal();
        a.setId(id);
        a.setNome(nome);
        a.setEspecie(Especie.CAO);
        a.setEstadoSaude(EstadoSaude.NORMAL);
        a.setTutor(tutor);
        return a;
    }
}
