package pt.hotel.animais.service;

import pt.hotel.animais.dto.NotaDto;
import pt.hotel.animais.dto.NotaFormDto;

public interface INotaService {
    NotaDto create(NotaFormDto form, Long autorId);
}
