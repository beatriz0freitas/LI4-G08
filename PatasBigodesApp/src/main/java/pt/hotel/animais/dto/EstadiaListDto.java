package pt.hotel.animais.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.hotel.animais.model.enums.EstadoEstadia;

import java.time.LocalDateTime;

/**
 * DTO para listagem de estadias na interface.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadiaListDto {
    
    private Long id;
    private Long reservaId;
    private String animalNome;
    private String alojamentoIdentificacao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String duracao; // Formato legível: "2 dias, 3 horas"
    private EstadoEstadia estado;
    private String estadoLabel;
    private String estadoCss;
}
