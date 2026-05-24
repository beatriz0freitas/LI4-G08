package pt.hotel.animais.service;

import pt.hotel.animais.model.Alojamento;

import java.util.List;

public interface ILimpezaService {
    List<Alojamento> listarAlojamentosPendentes();

    boolean marcarComoLimpo(Long id);
}
