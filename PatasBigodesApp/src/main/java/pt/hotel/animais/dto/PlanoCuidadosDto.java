package pt.hotel.animais.dto;

import java.util.List;

public class PlanoCuidadosDto {
    private Long estadiaId;
    private Long animalId;
    private List<RegistoCuidadoDto> itens;

    public Long getEstadiaId() { return estadiaId; }
    public void setEstadiaId(Long estadiaId) { this.estadiaId = estadiaId; }
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }
    public List<RegistoCuidadoDto> getItens() { return itens; }
    public void setItens(List<RegistoCuidadoDto> itens) { this.itens = itens; }
}
