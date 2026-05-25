package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pt.hotel.animais.dto.ColaboradorFormDto;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.enums.TipoColaborador;
import pt.hotel.animais.repository.ColaboradorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        assertThat(criado.getUsername()).isEqualTo("novo");
        assertThat(criado.getEmail()).isEqualTo("novo@hotel.local");
        assertThat(criado.isAtivo()).isTrue();
        verify(eventPublisher).publishEvent(any());
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
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void listarTodosDeveDelegarParaRepositorio() {
        Colaborador c = new Colaborador();
        c.setId(1L);
        when(repository.findAll()).thenReturn(List.of(c));

        List<Colaborador> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    void obterDeveRetornarColaboradorExistente() {
        Colaborador c = new Colaborador();
        c.setId(5L);
        c.setNome("Teste");
        when(repository.findById(5L)).thenReturn(Optional.of(c));

        Colaborador resultado = service.obter(5L);

        assertThat(resultado.getNome()).isEqualTo("Teste");
    }

    @Test
    void obterDeveLancarExcecaoSeNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obter(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void atualizarDeveAlterarCamposEGuardar() {
        Colaborador existente = new Colaborador();
        existente.setId(1L);
        existente.setUsername("antigo");
        existente.setNome("Antigo Nome");
        existente.setEmail("antigo@hotel.local");
        existente.setAtivo(false);

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByUsernameAndIdNot(eq("novo"), eq(1L))).thenReturn(false);
        when(repository.existsByEmailAndIdNot(eq("novo@hotel.local"), eq(1L))).thenReturn(false);
        when(repository.save(any(Colaborador.class))).thenAnswer(inv -> inv.getArgument(0));

        ColaboradorFormDto form = form();
        Colaborador atualizado = service.atualizar(1L, form);

        assertThat(atualizado.getNome()).isEqualTo("Novo Colaborador");
        assertThat(atualizado.getUsername()).isEqualTo("novo");
        assertThat(atualizado.getEmail()).isEqualTo("novo@hotel.local");
        assertThat(atualizado.isAtivo()).isTrue();
        assertThat(atualizado.getTipoColaborador()).isEqualTo(TipoColaborador.CUIDADOR);
        assertThat(encoder.matches("segredo123", atualizado.getPasswordHash())).isTrue();
        verify(repository).save(existente);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void atualizarSemNovaPasswordNaoAlteraPasswordHash() {
        Colaborador existente = new Colaborador();
        existente.setId(2L);
        existente.setPasswordHash("$2a$hash-original");

        ColaboradorFormDto form = form();
        form.setPassword("");

        when(repository.findById(2L)).thenReturn(Optional.of(existente));
        when(repository.existsByUsernameAndIdNot(eq("novo"), eq(2L))).thenReturn(false);
        when(repository.existsByEmailAndIdNot(eq("novo@hotel.local"), eq(2L))).thenReturn(false);
        when(repository.save(any(Colaborador.class))).thenAnswer(inv -> inv.getArgument(0));

        Colaborador atualizado = service.atualizar(2L, form);

        assertThat(atualizado.getPasswordHash()).isEqualTo("$2a$hash-original");
    }

    @Test
    void atualizarComUsernameConflituosoDeveFalhar() {
        Colaborador existente = new Colaborador();
        existente.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByUsernameAndIdNot("novo", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(1L, form()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username");
    }

    @Test
    void criarComUsernameExistenteDeveFalhar() {
        when(repository.existsByUsername("novo")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(form()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username");
    }

    @Test
    void criarComEmailExistenteDeveFalhar() {
        when(repository.existsByUsername("novo")).thenReturn(false);
        when(repository.existsByEmail("novo@hotel.local")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(form()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
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
