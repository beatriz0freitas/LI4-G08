package pt.hotel.animais.service;

import pt.hotel.animais.dto.AnimalFormDto;
import pt.hotel.animais.model.Animal;

import java.util.List;

public interface IAnimalService {
    Animal registar(AnimalFormDto formDto);

    Animal obter(Long id);

    List<Animal> procurarPorTutor(Long tutorId);

    List<Animal> procurarPorNome(String nome);

    List<Animal> listarTodos();

    Animal atualizar(Long id, AnimalFormDto formDto);

    void eliminar(Long id);
}
