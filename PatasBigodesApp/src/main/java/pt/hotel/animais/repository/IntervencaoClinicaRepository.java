package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.IntervencaoClinica;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface IntervencaoClinicaRepository extends JpaRepository<IntervencaoClinica, Long> {
    List<IntervencaoClinica> findByEstadiaId(Long estadiaId);

    List<IntervencaoClinica> findByEstadiaReservaAnimalIdOrderByDataHoraDesc(Long animalId);

    /**
     * Soma de custos de intervenções clínicas para uma estadia.
     */
    @Query("SELECT COALESCE(SUM(ic.custo), 0) FROM IntervencaoClinica ic WHERE ic.estadia.id = :estadiaId")
    BigDecimal sumCustoByEstadiaId(@Param("estadiaId") Long estadiaId);
}
