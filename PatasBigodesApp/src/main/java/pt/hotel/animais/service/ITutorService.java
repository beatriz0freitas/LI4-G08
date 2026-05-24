package pt.hotel.animais.service;

import pt.hotel.animais.dto.TutorFormDto;
import pt.hotel.animais.model.Tutor;

import java.util.List;

public interface ITutorService {
    Tutor registar(TutorFormDto formDto);

    Tutor procurarPorNif(String nif);

    List<Tutor> procurarPorNome(String nome);

    Tutor obter(Long id);

    List<Tutor> listarTodos();

    Tutor atualizar(Long id, TutorFormDto formDto);

    void eliminar(Long id);
}
