package pt.hotel.animais.service;

import pt.hotel.animais.dto.ColaboradorFormDto;
import pt.hotel.animais.model.Colaborador;

import java.util.List;

public interface IColaboradorService {
    List<Colaborador> listarTodos();
    Colaborador obter(Long id);
    Colaborador criar(ColaboradorFormDto formDto);
    Colaborador atualizar(Long id, ColaboradorFormDto formDto);
    void desativar(Long id);
}
