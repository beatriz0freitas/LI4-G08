package pt.hotel.animais.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.hotel.animais.model.enums.EstadoReserva;

import java.time.LocalDate;

/**
 * DTO para listagem de reservas na interface.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaListDto {
    
    private Long id;
    private String animalNome;
    private String tutorNome;
    private String alojamentoIdentificacao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private EstadoReserva estado;
    private String estadoLabel;
    private String estadoCss;
}
