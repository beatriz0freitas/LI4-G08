package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.config.SecurityConfig;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeDto;
import pt.hotel.animais.dto.FichaClinicaDto;
import pt.hotel.animais.dto.IntervencaoClinicaDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoSaude;
import pt.hotel.animais.service.IAlteracaoEstadoSaudeService;
import pt.hotel.animais.service.IClinicaService;
import pt.hotel.animais.service.IIntervencaoClinicaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = ClinicaController.class)
@Import(SecurityConfig.class)
class ClinicaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IIntervencaoClinicaService intervencaoClinicaService;

    @MockBean
    private IAlteracaoEstadoSaudeService alteracaoEstadoSaudeService;

    @MockBean
    private IClinicaService clinicaService;

    @Test
    @WithMockUser(roles = {"MEDICO_VETERINARIO"})
    void indexDeveRenderizarPaginaDaClinica() throws Exception {
        when(clinicaService.listarAnimais()).thenReturn(List.of());

        mvc.perform(get("/clinica"))
            .andExpect(status().isOk())
            .andExpect(view().name("clinica/index"));
    }

    @Test
    @WithMockUser(roles = {"MEDICO_VETERINARIO"})
    void detalheAnimalDeveRenderizarFichaClinica() throws Exception {
        Animal animal = animal(7L);
        when(clinicaService.obterFichaAnimal(7L)).thenReturn(new FichaClinicaDto(animal, null, List.of()));

        mvc.perform(get("/clinica/animais/7"))
            .andExpect(status().isOk())
            .andExpect(view().name("clinica/detalhe"));
    }

    @Test
    @WithMockUser(username = "9", roles = {"MEDICO_VETERINARIO"})
    void createIntervencaoDeveRedirecionarParaLista() throws Exception {
        IntervencaoClinicaDto dto = new IntervencaoClinicaDto();
        dto.setId(1L);
        dto.setEstadiaId(1L);
        dto.setDescricao("Vacina");
        dto.setCusto(new BigDecimal("20.00"));
        dto.setDataHora(LocalDateTime.now());

        when(intervencaoClinicaService.register(any())).thenReturn(dto);
        when(clinicaService.obterAnimalIdPorEstadia(1L)).thenReturn(Optional.of(7L));

        mvc.perform(post("/clinica/intervencoes/create")
                .with(csrf())
                .param("estadiaId", "1")
                .param("descricao", "Vacina")
                .param("custo", "20.00")
                .param("dataHora", LocalDateTime.now().toString()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/clinica/animais/7"));

        verify(intervencaoClinicaService).register(any());
    }

    @Test
    @WithMockUser(username = "9", roles = {"MEDICO_VETERINARIO"})
    void createAlteracaoEstadoDeveRedirecionarParaLista() throws Exception {
        AlteracaoEstadoSaudeDto dto = new AlteracaoEstadoSaudeDto();
        dto.setId(1L);
        dto.setEstadiaId(1L);
        dto.setDescricao("Melhora");
        dto.setSeveridade("BAIXA");
        dto.setDataHora(LocalDateTime.now());

        when(alteracaoEstadoSaudeService.register(any())).thenReturn(dto);
        when(clinicaService.obterAnimalIdPorEstadia(1L)).thenReturn(Optional.of(7L));

        mvc.perform(post("/clinica/alteracoes/create")
                .with(csrf())
                .param("estadiaId", "1")
                .param("descricao", "Melhora")
                .param("severidade", "BAIXA")
                .param("dataHora", LocalDateTime.now().toString()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/clinica/animais/7"));

        verify(alteracaoEstadoSaudeService).register(any());
    }

    private Animal animal(Long id) {
        Tutor tutor = new Tutor();
        tutor.setId(3L);
        tutor.setNome("Ana Silva");
        Animal animal = new Animal();
        animal.setId(id);
        animal.setTutor(tutor);
        animal.setNome("Simba");
        animal.setEspecie(Especie.CAO);
        animal.setRaca("Labrador");
        animal.setDataNascimento(LocalDate.of(2021, 1, 1));
        animal.setPeso(new BigDecimal("32.00"));
        animal.setEstadoSaude(EstadoSaude.NORMAL);
        return animal;
    }
}
