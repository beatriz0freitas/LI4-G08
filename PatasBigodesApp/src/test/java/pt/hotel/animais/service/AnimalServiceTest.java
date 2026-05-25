package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.dto.AnimalFormDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoSaude;
import pt.hotel.animais.repository.AnimalRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ITutorService tutorService;

    @InjectMocks
    private AnimalService animalService;

    @Test
    void registarDeveCriarAnimalAssociadoAoTutor() {
        Tutor tutor = criarTutor(1L);
        AnimalFormDto form = criarForm(1L, "Rex", Especie.CAO);

        when(tutorService.obter(1L)).thenReturn(tutor);
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> {
            Animal a = inv.getArgument(0);
            a.setId(10L);
            return a;
        });

        Animal resultado = animalService.registar(form);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getNome()).isEqualTo("Rex");
        assertThat(resultado.getEspecie()).isEqualTo(Especie.CAO);
        assertThat(resultado.getTutor()).isSameAs(tutor);
        verify(animalRepository).save(any(Animal.class));
    }

    @Test
    void registarDeveRejeitarQuandoTutorNaoExiste() {
        AnimalFormDto form = criarForm(99L, "Rex", Especie.CAO);
        when(tutorService.obter(99L)).thenThrow(new IllegalArgumentException("Tutor com ID 99 não encontrado"));

        assertThatThrownBy(() -> animalService.registar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");

        verify(animalRepository, never()).save(any());
    }

    @Test
    void obterDeveRetornarAnimalExistente() {
        Animal animal = criarAnimal(5L, criarTutor(1L));
        when(animalRepository.findById(5L)).thenReturn(Optional.of(animal));

        Animal resultado = animalService.obter(5L);

        assertThat(resultado.getId()).isEqualTo(5L);
    }

    @Test
    void obterDeveLancarExcecaoQuandoNaoEncontrado() {
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> animalService.obter(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    void procurarPorNomeDeveRetornarResultados() {
        Animal animal = criarAnimal(1L, criarTutor(1L));
        when(animalRepository.findByNomeContainingIgnoreCase("rex")).thenReturn(List.of(animal));

        List<Animal> resultado = animalService.procurarPorNome("rex");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void procurarPorNomeDeveRejeitarNomeVazio() {
        assertThatThrownBy(() -> animalService.procurarPorNome(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> animalService.procurarPorNome(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void atualizarDeveModificarDadosDoAnimal() {
        Animal animal = criarAnimal(5L, criarTutor(1L));
        AnimalFormDto form = criarForm(1L, "Max", Especie.GATO);

        when(animalRepository.findById(5L)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> inv.getArgument(0));

        Animal resultado = animalService.atualizar(5L, form);

        assertThat(resultado.getNome()).isEqualTo("Max");
        assertThat(resultado.getEspecie()).isEqualTo(Especie.GATO);
    }

    @Test
    void eliminarDeveChamarDeleteNoRepositorio() {
        Animal animal = criarAnimal(5L, criarTutor(1L));
        when(animalRepository.findById(5L)).thenReturn(Optional.of(animal));

        animalService.eliminar(5L);

        verify(animalRepository).delete(animal);
    }

    private Tutor criarTutor(Long id) {
        Tutor tutor = new Tutor();
        tutor.setId(id);
        tutor.setNome("Tutor " + id);
        tutor.setNif("20000000" + id);
        tutor.setContacto("910000000");
        tutor.setEmail("tutor" + id + "@test.local");
        return tutor;
    }

    private Animal criarAnimal(Long id, Tutor tutor) {
        Animal animal = new Animal();
        animal.setId(id);
        animal.setTutor(tutor);
        animal.setNome("Animal " + id);
        animal.setEspecie(Especie.CAO);
        animal.setRaca("SRD");
        animal.setDataNascimento(LocalDate.now().minusYears(2));
        animal.setPeso(new BigDecimal("8.0"));
        animal.setEstadoSaude(EstadoSaude.NORMAL);
        return animal;
    }

    private AnimalFormDto criarForm(Long tutorId, String nome, Especie especie) {
        AnimalFormDto form = new AnimalFormDto();
        form.setTutorId(tutorId);
        form.setNome(nome);
        form.setEspecie(especie);
        form.setRaca("SRD");
        form.setDataNascimento(LocalDate.now().minusYears(2));
        form.setPeso(new BigDecimal("8.0"));
        form.setEstadoSaude(EstadoSaude.NORMAL);
        return form;
    }
}
