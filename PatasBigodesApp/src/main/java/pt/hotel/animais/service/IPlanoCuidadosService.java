package pt.hotel.animais.service;

import pt.hotel.animais.dto.PlanoCuidadosDto;

public interface IPlanoCuidadosService {
    PlanoCuidadosDto getPlanoByEstadia(Long estadiaId);
}
