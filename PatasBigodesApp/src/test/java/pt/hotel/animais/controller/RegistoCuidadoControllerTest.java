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
import pt.hotel.animais.dto.RegistoCuidadoDto;
import pt.hotel.animais.service.IRegistoCuidadoService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = RegistoCuidadoController.class)
@Import(SecurityConfig.class)
class RegistoCuidadoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IRegistoCuidadoService registoCuidadoService;

    @Test
    @WithMockUser(username = "7", roles = {"CUIDADOR"})
    void listarDeveRenderizarRegistosDaEstadia() throws Exception {
        RegistoCuidadoDto dto = new RegistoCuidadoDto();
        dto.setId(1L);
        dto.setEstadiaId(10L);
        dto.setDescricao("Alimentado");
        dto.setDataHora(LocalDateTime.now());
        dto.setAutorNome("7");

        when(registoCuidadoService.listByEstadia(eq(10L), any()))
            .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

        mvc.perform(get("/cuidados").param("estadiaId", "10"))
            .andExpect(status().isOk())
            .andExpect(view().name("cuidados/registos"))
            .andExpect(model().attribute("estadiaId", 10L));
    }

    @Test
    @WithMockUser(username = "7", roles = {"CUIDADOR"})
    void createDeveRedirecionarParaListaDaEstadia() throws Exception {
        RegistoCuidadoDto dto = new RegistoCuidadoDto();
        dto.setId(1L);
        dto.setEstadiaId(10L);
        dto.setDescricao("Alimentado");
        dto.setDataHora(LocalDateTime.now());
        dto.setAutorNome("7");

        when(registoCuidadoService.create(any(), eq(7L))).thenReturn(dto);

        mvc.perform(post("/cuidados/create")
                .with(csrf())
                .param("estadiaId", "10")
                .param("descricao", "Alimentado")
                .param("dataHora", LocalDateTime.now().toString()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/cuidados?estadiaId=10"));

        verify(registoCuidadoService).create(any(), eq(7L));
    }
}
