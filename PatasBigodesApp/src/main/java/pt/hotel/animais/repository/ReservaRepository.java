package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.EstadoReserva;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository para a entidade Reserva.
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    /**
     * Procura reservas de um tutor.
     */
    List<Reserva> findByTutorId(Long tutorId);
    
    /**
     * Procura reservas de um animal.
     */
    List<Reserva> findByAnimalId(Long animalId);
    
    /**
     * Procura reservas de um alojamento.
     */
    List<Reserva> findByAlojamentoId(Long alojamentoId);
    
    /**
     * Procura reservas ativas de um alojamento dentro de um período.
     * Utilizado para verificar disponibilidade.
     */
    @Query("SELECT r FROM Reserva r WHERE r.alojamento.id = :alojamentoId " +
           "AND r.estado = pt.hotel.animais.model.enums.EstadoReserva.ATIVA " +
           "AND NOT (r.dataFim < :dataInicio OR r.dataInicio > :dataFim)")
    List<Reserva> findActiveReservasInPeriod(
        @Param("alojamentoId") Long alojamentoId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
    
    /**
     * Procura reservas ativas de um animal.
     */
    List<Reserva> findByAnimalIdAndEstado(Long animalId, EstadoReserva estado);
    
    /**
     * Procura reservas ativas de um tutor.
     */
    List<Reserva> findByTutorIdAndEstado(Long tutorId, EstadoReserva estado);
    
    /**
     * Procura reservas de um tutor ordenadas por data de criação decrescente (mais recentes primeiro).
     */
    List<Reserva> findByTutorIdOrderByDataCriacaoDesc(Long tutorId);
    
    /**
     * Conta as reservas ativas num determinado período para um alojamento.
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.alojamento.id = :alojamentoId " +
           "AND r.estado = pt.hotel.animais.model.enums.EstadoReserva.ATIVA " +
           "AND NOT (r.dataFim < :dataInicio OR r.dataInicio > :dataFim)")
    long countActiveInPeriod(
        @Param("alojamentoId") Long alojamentoId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
}
