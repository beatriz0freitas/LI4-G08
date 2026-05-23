package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.Pagamento;

import java.math.BigDecimal;

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

}
