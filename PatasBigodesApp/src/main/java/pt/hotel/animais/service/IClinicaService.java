package pt.hotel.animais.service;

import pt.hotel.animais.dto.FichaClinicaDto;
import pt.hotel.animais.model.Animal;

import java.util.List;
import java.util.Optional;

public interface IClinicaService {
    List<Animal> listarAnimais();

    FichaClinicaDto obterFichaAnimal(Long animalId);

    Optional<Long> obterAnimalIdPorEstadia(Long estadiaId);
}
