package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.dto.TutorFormDto;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.repository.TutorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private TutorService tutorService;

    @Test
    void registarDevePersistirTutorComNifUnico() {
        TutorFormDto form = criarForm("Maria Silva", "123456789");

        when(tutorRepository.findByNif("123456789")).thenReturn(Optional.empty());
        when(tutorRepository.save(any(Tutor.class))).thenAnswer(inv -> {
            Tutor t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        Tutor resultado = tutorService.registar(form);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNif()).isEqualTo("123456789");
        assertThat(resultado.getNome()).isEqualTo("Maria Silva");
        verify(tutorRepository).save(any(Tutor.class));
    }

    @Test
    void registarDeveRejeitarNifDuplicado() {
        TutorFormDto form = criarForm("João Costa", "123456789");
        when(tutorRepository.findByNif("123456789")).thenReturn(Optional.of(new Tutor()));

        assertThatThrownBy(() -> tutorService.registar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NIF");

        verify(tutorRepository, never()).save(any());
    }

    @Test
    void obterDeveRetornarTutorExistente() {
        Tutor tutor = criarTutor(2L, "123456789");
        when(tutorRepository.findById(2L)).thenReturn(Optional.of(tutor));

        Tutor resultado = tutorService.obter(2L);

        assertThat(resultado.getId()).isEqualTo(2L);
    }

    @Test
    void obterDeveLancarExcecaoParaIdInexistente() {
        when(tutorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.obter(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    void procurarPorNifDeveRetornarTutorCorreto() {
        Tutor tutor = criarTutor(1L, "987654321");
        when(tutorRepository.findByNif("987654321")).thenReturn(Optional.of(tutor));

        Tutor resultado = tutorService.procurarPorNif("987654321");

        assertThat(resultado.getNif()).isEqualTo("987654321");
    }

    @Test
    void procurarPorNifDeveLancarExcecaoSeNaoEncontrado() {
        when(tutorRepository.findByNif("000000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.procurarPorNif("000000000"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void procurarPorNomeDeveRejeitarNomeVazio() {
        assertThatThrownBy(() -> tutorService.procurarPorNome(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> tutorService.procurarPorNome(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void procurarPorNomeDeveRetornarResultados() {
        Tutor tutor = criarTutor(1L, "123456789");
        when(tutorRepository.findByNomeContainingIgnoreCase("Maria")).thenReturn(List.of(tutor));

        List<Tutor> resultado = tutorService.procurarPorNome("Maria");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void atualizarDevePermitirMesmoNif() {
        Tutor tutor = criarTutor(1L, "123456789");
        TutorFormDto form = criarForm("Maria Novo", "123456789");

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(tutorRepository.save(any(Tutor.class))).thenAnswer(inv -> inv.getArgument(0));

        Tutor resultado = tutorService.atualizar(1L, form);

        assertThat(resultado.getNome()).isEqualTo("Maria Novo");
        verify(tutorRepository, never()).findByNif(any());
    }

    @Test
    void atualizarDeveRejeitarNifNovoQueJaExiste() {
        Tutor tutor = criarTutor(1L, "123456789");
        TutorFormDto form = criarForm("Maria Nova", "999999999");

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(tutorRepository.findByNif("999999999")).thenReturn(Optional.of(new Tutor()));

        assertThatThrownBy(() -> tutorService.atualizar(1L, form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NIF");
    }

    @Test
    void eliminarDeveChamarDeleteNoRepositorio() {
        Tutor tutor = criarTutor(1L, "123456789");
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));

        tutorService.eliminar(1L);

        verify(tutorRepository).delete(tutor);
    }

    private TutorFormDto criarForm(String nome, String nif) {
        TutorFormDto form = new TutorFormDto();
        form.setNome(nome);
        form.setNif(nif);
        form.setContacto("910000000");
        form.setEmail("tutor@test.local");
        return form;
    }

    private Tutor criarTutor(Long id, String nif) {
        Tutor tutor = new Tutor();
        tutor.setId(id);
        tutor.setNome("Tutor " + id);
        tutor.setNif(nif);
        tutor.setContacto("910000000");
        tutor.setEmail("tutor" + id + "@test.local");
        return tutor;
    }
}
