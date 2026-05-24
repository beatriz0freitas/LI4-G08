package pt.hotel.animais.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;

import java.time.LocalDateTime;

@Repository
public interface EstadiaRepository extends JpaRepository<Estadia, Long> {

	@Query(value = """
		SELECT e FROM Estadia e
		JOIN e.reserva r
		JOIN r.tutor t
		JOIN r.animal a
		WHERE (:clienteId IS NULL OR t.id = :clienteId)
		  AND (:animalId IS NULL OR a.id = :animalId)
		  AND (:estado IS NULL OR e.estado = :estado)
		  AND (:dataInicio IS NULL OR e.dataInicio >= :dataInicio)
		  AND (:dataFim IS NULL OR e.dataFim <= :dataFim)
		ORDER BY e.dataCriacao DESC
		""",
		countQuery = """
		SELECT COUNT(e) FROM Estadia e
		JOIN e.reserva r
		JOIN r.tutor t
		JOIN r.animal a
		WHERE (:clienteId IS NULL OR t.id = :clienteId)
		  AND (:animalId IS NULL OR a.id = :animalId)
		  AND (:estado IS NULL OR e.estado = :estado)
		  AND (:dataInicio IS NULL OR e.dataInicio >= :dataInicio)
		  AND (:dataFim IS NULL OR e.dataFim <= :dataFim)
		""")
	Page<Estadia> pesquisarHistorico(
		@Param("clienteId") Long clienteId,
		@Param("animalId") Long animalId,
		@Param("estado") EstadoEstadia estado,
		@Param("dataInicio") LocalDateTime dataInicio,
		@Param("dataFim") LocalDateTime dataFim,
		Pageable pageable
	);

	/**
	 * Conta estadias por estado.
	 */
	@Query("SELECT COUNT(e) FROM Estadia e WHERE e.estado = :estado")
	long countByEstado(@Param("estado") EstadoEstadia estado);

	@Query("""
		SELECT COUNT(e)
		FROM Estadia e
		WHERE e.dataInicio <= :fim
		  AND (e.dataFim IS NULL OR e.dataFim >= :inicio)
		""")
	long countSobrepostasPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

	@Query("""
		SELECT COUNT(DISTINCT e.reserva.alojamento.id)
		FROM Estadia e
		WHERE e.estado = pt.hotel.animais.model.enums.EstadoEstadia.EM_CURSO
		""")
	long countAlojamentosOcupadosAgora();

	java.util.Optional<Estadia> findByReservaId(Long reservaId);

}
