package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.TipoAlojamentoTarifa;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para gestão de tarifas por tipo de alojamento.
 */
@Repository
public interface TipoAlojamentoTarifaRepository extends JpaRepository<TipoAlojamentoTarifa, Long> {

    /**
     * Busca a tarifa ativa para um tipo de alojamento específico.
     */
    @Query("SELECT tat FROM TipoAlojamentoTarifa tat WHERE tat.tipoAlojamento = :tipo AND tat.ativo = true")
    Optional<TipoAlojamentoTarifa> findActivoByTipo(@Param("tipo") String tipo);

    /**
     * Busca qualquer tarifa (ativa ou não) para um tipo de alojamento.
     */
    Optional<TipoAlojamentoTarifa> findByTipoAlojamento(String tipo);

    /**
     * Lista todos os tipos de alojamento com tarifas ativas.
     */
    @Query("SELECT tat FROM TipoAlojamentoTarifa tat WHERE tat.ativo = true ORDER BY tat.tipoAlojamento ASC")
    List<TipoAlojamentoTarifa> findAllAtivos();

    /**
     * Lista todos os tipos de alojamento com tarifas (ativos e inativos).
     */
    @Query("SELECT tat FROM TipoAlojamentoTarifa tat ORDER BY tat.tipoAlojamento ASC")
    List<TipoAlojamentoTarifa> findAllOrderByTipo();
}
