package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pt.hotel.animais.dto.ColaboradorFormDto;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.enums.TipoColaborador;
import pt.hotel.animais.repository.ColaboradorRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ColaboradorServiceTest {

    private final ColaboradorRepository repository = mock(ColaboradorRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final ColaboradorService service = new ColaboradorService(repository, encoder, eventPublisher);

    @Test
    void criarDeveCodificarPasswordEGuardarTipoEnum() {
        ColaboradorFormDto form = form();
        when(repository.existsByUsername("novo")).thenReturn(false);
        when(repository.existsByEmail("novo@hotel.local")).thenReturn(false);
        when(repository.save(any(Colaborador.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Colaborador criado = service.criar(form);

        assertThat(criado.getTipoColaborador()).isEqualTo(TipoColaborador.CUIDADOR);
        assertThat(criado.getPasswordHash()).isNotEqualTo("segredo123");
        assertThat(encoder.matches("segredo123", criado.getPasswordHash())).isTrue();
    }

    @Test
    void criarSemPasswordDeveFalhar() {
        ColaboradorFormDto form = form();
        form.setPassword("");

        assertThatThrownBy(() -> service.criar(form))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Password");
    }

    @Test
    void desativarDeveManterRegistoEAlterarEstado() {
        Colaborador colaborador = new Colaborador();
        colaborador.setId(10L);
        colaborador.setAtivo(true);
        when(repository.findById(10L)).thenReturn(Optional.of(colaborador));
        when(repository.save(any(Colaborador.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.desativar(10L);

        assertThat(colaborador.isAtivo()).isFalse();
    }

    private ColaboradorFormDto form() {
        ColaboradorFormDto form = new ColaboradorFormDto();
        form.setUsername("novo");
        form.setNome("Novo Colaborador");
        form.setEmail("novo@hotel.local");
        form.setPassword("segredo123");
        form.setTipoColaborador(TipoColaborador.CUIDADOR);
        form.setAtivo(true);
        return form;
    }
}
