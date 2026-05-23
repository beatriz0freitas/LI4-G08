package pt.hotel.animais.dto;

import java.util.List;

public class PlanoCuidadosView {
    private Long estadiaId;
    private Long animalId;
    private List<RegistoCuidadoView> itens;

    public Long getEstadiaId() { return estadiaId; }
    public void setEstadiaId(Long estadiaId) { this.estadiaId = estadiaId; }
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }
    public List<RegistoCuidadoView> getItens() { return itens; }
    public void setItens(List<RegistoCuidadoView> itens) { this.itens = itens; }
}
