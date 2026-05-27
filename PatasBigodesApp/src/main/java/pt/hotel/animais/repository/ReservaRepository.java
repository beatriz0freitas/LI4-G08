package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.EstadoReserva;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Reserva.
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT r FROM Reserva r " +
           "JOIN FETCH r.tutor " +
           "JOIN FETCH r.animal " +
           "JOIN FETCH r.alojamento " +
           "ORDER BY r.dataCriacao DESC")
    List<Reserva> findAllWithDetalhes();

    @Query("SELECT r FROM Reserva r " +
           "JOIN FETCH r.tutor " +
           "JOIN FETCH r.animal " +
           "JOIN FETCH r.alojamento " +
           "WHERE r.id = :id")
    Optional<Reserva> findWithDetalhesById(@Param("id") Long id);
    
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
     * Procura reservas ativas ou confirmadas de um alojamento dentro de um período.
     * Utilizado para verificar disponibilidade.
     */
    @Query("SELECT r FROM Reserva r WHERE r.alojamento.id = :alojamentoId " +
           "AND r.estado IN (pt.hotel.animais.model.enums.EstadoReserva.ATIVA, pt.hotel.animais.model.enums.EstadoReserva.CONFIRMADA) " +
           "AND NOT (r.dataFim < :dataInicio OR r.dataInicio > :dataFim)")
    List<Reserva> findActiveReservasInPeriod(
        @Param("alojamentoId") Long alojamentoId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    @Query("SELECT r FROM Reserva r " +
           "JOIN FETCH r.animal " +
           "JOIN FETCH r.alojamento " +
           "WHERE r.alojamento.id = :alojamentoId " +
           "AND r.estado IN (pt.hotel.animais.model.enums.EstadoReserva.ATIVA, pt.hotel.animais.model.enums.EstadoReserva.CONFIRMADA) " +
           "AND NOT (r.dataFim < :dataInicio OR r.dataInicio > :dataFim) " +
           "ORDER BY r.dataInicio ASC")
    List<Reserva> findActiveReservasInPeriodWithDetalhes(
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

       List<Reserva> findByDataCriacaoBetweenOrderByDataCriacaoAsc(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Procura reservas por estado ordenadas por data de início decrescente.
     */
    List<Reserva> findByEstadoOrderByDataInicioDesc(EstadoReserva estado);

    /**
     * Conta as reservas num determinado estado.
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.estado = :estado")
    long countByEstado(@Param("estado") EstadoReserva estado);

    /**
     * Conta reservas ativas ou confirmadas com data de início futura.
     */
    @Query("SELECT COUNT(r) FROM Reserva r " +
           "WHERE r.estado IN (pt.hotel.animais.model.enums.EstadoReserva.ATIVA, pt.hotel.animais.model.enums.EstadoReserva.CONFIRMADA) " +
           "AND r.dataInicio > :hoje")
    long countFuturas(@Param("hoje") LocalDate hoje);
    
    /**
     * Conta as reservas ativas ou confirmadas num determinado período para um alojamento.
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.alojamento.id = :alojamentoId " +
           "AND r.estado IN (pt.hotel.animais.model.enums.EstadoReserva.ATIVA, pt.hotel.animais.model.enums.EstadoReserva.CONFIRMADA) " +
           "AND NOT (r.dataFim < :dataInicio OR r.dataInicio > :dataFim)")
    long countActiveInPeriod(
        @Param("alojamentoId") Long alojamentoId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    @Query("SELECT COUNT(r) FROM Reserva r " +
           "WHERE NOT (r.dataFim < :dataInicio OR r.dataInicio > :dataFim)")
    long countInPeriod(
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
}
