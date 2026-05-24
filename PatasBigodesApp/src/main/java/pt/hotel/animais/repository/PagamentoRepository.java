package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.Pagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

	/**
	 * Soma o valor total dos pagamentos.
	 */
	@Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pagamento p")
	BigDecimal sumValorTotal();

	/**
	 * Conta os pagamentos ainda não liquidados.
	 */
	@Query("SELECT COUNT(p) FROM Pagamento p WHERE p.estadoPagamento = pt.hotel.animais.model.enums.EstadoPagamento.PENDENTE")
	long countPendentes();

	@Query("""
		SELECT COALESCE(SUM(p.valor), 0)
		FROM Pagamento p
		WHERE p.dataCriacao BETWEEN :inicio AND :fim
		""")
	BigDecimal sumValorPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

	@Query("""
		SELECT p.metodoPagamento, COALESCE(SUM(p.valor), 0)
		FROM Pagamento p
		WHERE p.dataCriacao BETWEEN :inicio AND :fim
		GROUP BY p.metodoPagamento
		ORDER BY p.metodoPagamento
		""")
	List<Object[]> sumValorPorMetodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

	@Query("""
		SELECT COUNT(p)
		FROM Pagamento p
		WHERE p.estadoPagamento = pt.hotel.animais.model.enums.EstadoPagamento.PENDENTE
		  AND p.dataCriacao BETWEEN :inicio AND :fim
		""")
	long countPendentesPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

}
