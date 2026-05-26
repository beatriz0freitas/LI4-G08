package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.EstadoLimpeza;

import java.time.LocalDate;
import java.util.List;

public interface AlojamentoRepository extends JpaRepository<Alojamento, Long> {
    List<Alojamento> findByEstadoLimpeza(EstadoLimpeza estadoLimpeza);

    List<Alojamento> findByEstadoLimpezaAndTipoOrderByIdentificacaoAsc(
        EstadoLimpeza estadoLimpeza,
        String tipo
    );

    List<Alojamento> findAllByOrderByIdentificacaoAsc();

    long countByEstadoLimpeza(EstadoLimpeza estadoLimpeza);

    long countByTipo(String tipo);

    @Query("SELECT COUNT(DISTINCT r.alojamento.id) FROM Reserva r " +
           "WHERE r.estado = pt.hotel.animais.model.enums.EstadoReserva.ATIVA")
    long countAlojamentosComReservasAtivas();
    
    /**
     * Procura alojamentos disponíveis (limpos e sem conflitos de reserva) para um período específico.
     */
    @Query("SELECT DISTINCT a FROM Alojamento a " +
           "WHERE a.estadoLimpeza = pt.hotel.animais.model.enums.EstadoLimpeza.CONCLUIDO " +
           "AND a.id NOT IN (" +
           "  SELECT r.alojamento.id FROM Reserva r " +
           "  WHERE r.estado = pt.hotel.animais.model.enums.EstadoReserva.ATIVA " +
           "  AND NOT (r.dataFim < :dataInicio OR r.dataInicio > :dataFim)" +
           ") " +
           "ORDER BY a.identificacao ASC")
    List<Alojamento> findAvailableForPeriod(
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    /**
     * Procura alojamentos disponíveis e adequados ao tipo funcional pedido.
     */
    @Query("SELECT DISTINCT a FROM Alojamento a " +
           "WHERE a.estadoLimpeza = pt.hotel.animais.model.enums.EstadoLimpeza.CONCLUIDO " +
           "AND a.tipo = :tipo " +
           "AND a.id NOT IN (" +
           "  SELECT r.alojamento.id FROM Reserva r " +
           "  WHERE r.estado = pt.hotel.animais.model.enums.EstadoReserva.ATIVA " +
           "  AND NOT (r.dataFim < :dataInicio OR r.dataInicio > :dataFim)" +
           ") " +
           "ORDER BY a.identificacao ASC")
    List<Alojamento> findAvailableForPeriodAndTipo(
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim,
        @Param("tipo") String tipo
    );
    
    /**
     * Verifica se um alojamento específico está disponível para um período.
     */
    @Query("SELECT COUNT(r) FROM Reserva r " +
           "WHERE r.alojamento.id = :alojamentoId " +
           "AND r.estado = pt.hotel.animais.model.enums.EstadoReserva.ATIVA " +
           "AND NOT (r.dataFim < :dataInicio OR r.dataInicio > :dataFim)")
    long countConflictingReservas(
        @Param("alojamentoId") Long alojamentoId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
}
