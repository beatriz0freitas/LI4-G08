package pt.hotel.animais.service;

import pt.hotel.animais.dto.DisponibilidadeAlojamentoDto;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.Especie;

import java.time.LocalDate;
import java.util.List;

public interface IAlojamentoService {
    List<Alojamento> listarTodos();

    long contarAlojamentosDisponiveis();

    long contarAlojamentosPendentesLimpeza();

    List<DisponibilidadeAlojamentoDto> consultarDisponibilidade(LocalDate dataInicio, LocalDate dataFim);

    List<DisponibilidadeAlojamentoDto> consultarDisponibilidade(LocalDate dataInicio, LocalDate dataFim, Especie especie);

    boolean estaDisponivel(Long alojamentoId, LocalDate dataInicio, LocalDate dataFim);

    boolean estaDisponivel(Long alojamentoId, LocalDate dataInicio, LocalDate dataFim, Especie especie);

    Alojamento obter(Long id);

    void marcarPendenteLimpeza(Long alojamentoId);

    void marcarLimpezaConcluida(Long alojamentoId);
}
