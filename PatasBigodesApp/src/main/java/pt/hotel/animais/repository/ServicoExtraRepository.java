package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.ServicoExtra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServicoExtraRepository extends JpaRepository<ServicoExtra, Long> {
    List<ServicoExtra> findByEstadiaId(Long estadiaId);

    List<ServicoExtra> findByDataHoraBetweenOrderByDataHoraAsc(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Soma de custos de serviços extra para uma estadia.
     */
    @Query("SELECT COALESCE(SUM(s.custo), 0) FROM ServicoExtra s WHERE s.estadia.id = :estadiaId")
    BigDecimal sumCustoByEstadiaId(@Param("estadiaId") Long estadiaId);

    @Query("""
        SELECT COALESCE(SUM(s.custo), 0)
        FROM ServicoExtra s
        WHERE s.dataHora BETWEEN :inicio AND :fim
        """)
    BigDecimal sumCustoPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
