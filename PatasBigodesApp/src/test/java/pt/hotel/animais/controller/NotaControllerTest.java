package pt.hotel.animais.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.hotel.animais.config.SecurityConfig;
import pt.hotel.animais.dto.NotaDto;
import pt.hotel.animais.service.INotaService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotaController.class)
@Import(SecurityConfig.class)
class NotaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private INotaService notaService;

    @Test
    @WithMockUser(username = "11", roles = {"FUNCIONARIO_RECEPCAO"})
    void createNotaDeveRedirecionarParaReservas() throws Exception {
        NotaDto dto = new NotaDto();
        dto.setId(1L);
        dto.setReservaId(1L);
        dto.setDescricao("Passar turno");
        dto.setDataHora(LocalDateTime.now());

        when(notaService.create(any(), eq(11L))).thenReturn(dto);

        mvc.perform(post("/notas/create")
                .with(csrf())
                .param("reservaId", "1")
                .param("descricao", "Passar turno")
                .param("dataHora", LocalDateTime.now().toString()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/reservas"));

        verify(notaService).create(any(), eq(11L));
    }
}
