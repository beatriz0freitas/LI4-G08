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
import pt.hotel.animais.dto.ServicoExtraDto;
import pt.hotel.animais.service.IServicoExtraService;

import java.math.BigDecimal;
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

@WebMvcTest(controllers = ServicoExtraController.class)
@Import(SecurityConfig.class)
class ServicoExtraControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IServicoExtraService servicoExtraService;

    @Test
    @WithMockUser(username = "8", roles = {"CUIDADOR"})
    void listarDeveRenderizarExtrasDaEstadia() throws Exception {
        ServicoExtraDto dto = new ServicoExtraDto();
        dto.setId(1L);
        dto.setEstadiaId(20L);
        dto.setTipo("BANHO");
        dto.setCusto(new BigDecimal("15.00"));
        dto.setDataHora(LocalDateTime.now());

        when(servicoExtraService.listByEstadia(eq(20L), any()))
            .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

        mvc.perform(get("/extras").param("estadiaId", "20"))
            .andExpect(status().isOk())
            .andExpect(view().name("clinica/extras"))
            .andExpect(model().attribute("estadiaId", 20L));
    }

    @Test
    @WithMockUser(username = "8", roles = {"CUIDADOR"})
    void createDeveRedirecionarParaListaDaEstadia() throws Exception {
        ServicoExtraDto dto = new ServicoExtraDto();
        dto.setId(1L);
        dto.setEstadiaId(20L);
        dto.setTipo("BANHO");
        dto.setCusto(new BigDecimal("15.00"));
        dto.setDataHora(LocalDateTime.now());

        when(servicoExtraService.register(any(), eq(8L))).thenReturn(dto);

        mvc.perform(post("/extras/create")
                .with(csrf())
                .param("estadiaId", "20")
                .param("tipo", "BANHO")
                .param("custo", "15.00")
                .param("dataHora", LocalDateTime.now().toString()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/extras?estadiaId=20"));

        verify(servicoExtraService).register(any(), eq(8L));
    }
}
