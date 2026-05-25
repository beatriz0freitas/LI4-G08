package pt.hotel.animais.dto;

import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Estadia;

import java.util.List;

public class FichaClinicaDto {
    private final Animal animal;
    private final Estadia estadia;
    private final List<EventoClinicoDto> eventos;

    public FichaClinicaDto(Animal animal, Estadia estadia, List<EventoClinicoDto> eventos) {
        this.animal = animal;
        this.estadia = estadia;
        this.eventos = eventos;
    }

    public Animal getAnimal() {
        return animal;
    }

    public Estadia getEstadia() {
        return estadia;
    }

    public List<EventoClinicoDto> getEventos() {
        return eventos;
    }
}
