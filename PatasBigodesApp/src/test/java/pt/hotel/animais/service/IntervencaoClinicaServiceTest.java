package pt.hotel.animais.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pt.hotel.animais.dto.IntervencaoClinicaDto;
import pt.hotel.animais.dto.IntervencaoClinicaFormDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.IntervencaoClinica;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntervencaoClinicaServiceTest {

    @Mock
    private IntervencaoClinicaRepository intervencaoClinicaRepository;

    @Mock
    private EstadiaRepository estadiaRepository;

    @Mock
    private AuditoriaOperacaoService auditoriaOperacaoService;

    @InjectMocks
    private IntervencaoClinicaService service;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerDevePersistirIntervencaoComVeterinarioAutenticado() {
        autenticarVeterinario("9");
        Estadia estadia = criarEstadia(1L, EstadoEstadia.EM_CURSO);
        IntervencaoClinicaFormDto form = criarForm(1L, "Sutura", new BigDecimal("25.00"));

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(intervencaoClinicaRepository.save(any(IntervencaoClinica.class))).thenAnswer(inv -> {
            IntervencaoClinica ic = inv.getArgument(0);
            ic.setId(7L);
            return ic;
        });

        IntervencaoClinicaDto resultado = service.register(form, 9L);

        assertThat(resultado.getId()).isEqualTo(7L);
        assertThat(resultado.getEstadiaId()).isEqualTo(1L);
        assertThat(resultado.getCusto()).isEqualByComparingTo("25.00");
        ArgumentCaptor<IntervencaoClinica> captor = ArgumentCaptor.forClass(IntervencaoClinica.class);
        verify(intervencaoClinicaRepository).save(captor.capture());
        assertThat(captor.getValue().getMedicoId()).isEqualTo(9L);
        assertThat(captor.getValue().getDescricao()).isEqualTo("Sutura");
    }

    @Test
    void registerDeveRejeitarQuandoEstadiaNaoEstaEmCurso() {
        autenticarVeterinario("9");
        Estadia estadia = criarEstadia(1L, EstadoEstadia.TERMINADA);
        IntervencaoClinicaFormDto form = criarForm(1L, "Curativo", new BigDecimal("12.50"));

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));

        assertThatThrownBy(() -> service.register(form, 9L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("estadias em curso");

        verify(intervencaoClinicaRepository, never()).save(any());
    }

    @Test
    void registerDeveRejeitarQuandoUtilizadorNaoEhVeterinario() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("9", "senha", List.of(new SimpleGrantedAuthority("ROLE_CUIDADOR")))
        );
        IntervencaoClinicaFormDto form = criarForm(1L, "Medição", new BigDecimal("0.00"));

        assertThatThrownBy(() -> service.register(form, 9L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Apenas médico veterinário");

        verify(intervencaoClinicaRepository, never()).save(any());
    }

    @Test
    void registerDeveRejeitarCustoNegativo() {
        autenticarVeterinario("9");
        IntervencaoClinicaFormDto form = criarForm(1L, "Tratamento", new BigDecimal("-5.00"));

        assertThatThrownBy(() -> service.register(form, 9L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pode ser negativo");

        verify(intervencaoClinicaRepository, never()).save(any());
    }

    @Test
    void registerDeveRejeitarSemMedicoResponsavel() {
        autenticarVeterinario("9");
        IntervencaoClinicaFormDto form = criarForm(1L, "Observação", BigDecimal.ZERO);

        assertThatThrownBy(() -> service.register(form, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Médico responsável");

        verify(intervencaoClinicaRepository, never()).save(any());
    }

    @Test
    void registerDeveRejeitarDescricaoVazia() {
        autenticarVeterinario("9");
        IntervencaoClinicaFormDto form = criarForm(1L, " ", BigDecimal.ZERO);

        assertThatThrownBy(() -> service.register(form, 9L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Descrição");

        verify(intervencaoClinicaRepository, never()).save(any());
    }

    @Test
    void registerDeveRejeitarDataHoraObrigatoria() {
        autenticarVeterinario("9");
        IntervencaoClinicaFormDto form = criarForm(1L, "Observação", BigDecimal.ZERO);
        form.setDataHora(null);

        assertThatThrownBy(() -> service.register(form, 9L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data/hora");

        verify(intervencaoClinicaRepository, never()).save(any());
    }

    @Test
    void listByEstadiaDeveRetornarPaginaComIntervencoes() {
        IntervencaoClinica ic = criarIntervencao(1L, criarEstadia(1L, EstadoEstadia.EM_CURSO));
        when(intervencaoClinicaRepository.findByEstadiaId(1L)).thenReturn(List.of(ic));

        Page<IntervencaoClinicaDto> resultado = service.listByEstadia(1L, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().getFirst().getDescricao()).isEqualTo("Intervenção 1");
    }

    private void autenticarVeterinario(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, "senha", List.of(new SimpleGrantedAuthority("ROLE_MEDICO_VETERINARIO")))
        );
    }

    private Estadia criarEstadia(Long id, EstadoEstadia estado) {
        Estadia e = new Estadia();
        e.setId(id);
        e.setEstado(estado);
        e.setDataInicio(LocalDateTime.now().minusHours(1));
        return e;
    }

    private IntervencaoClinicaFormDto criarForm(Long estadiaId, String descricao, BigDecimal custo) {
        IntervencaoClinicaFormDto form = new IntervencaoClinicaFormDto();
        form.setEstadiaId(estadiaId);
        form.setDescricao(descricao);
        form.setCusto(custo);
        form.setDataHora(LocalDateTime.now());
        return form;
    }

    private IntervencaoClinica criarIntervencao(Long id, Estadia estadia) {
        IntervencaoClinica ic = new IntervencaoClinica();
        ic.setId(id);
        ic.setEstadia(estadia);
        ic.setDescricao("Intervenção " + id);
        ic.setCusto(new BigDecimal("10.00"));
        ic.setDataHora(LocalDateTime.now());
        ic.setMedicoId(9L);
        return ic;
    }
}
