package pt.hotel.animais.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.PlanoCuidados;

import java.util.Optional;

/**
 * Repositório para PlanoCuidados.
 * Suporta o duplo vínculo (Animal histórico + Estadia ativa)
 */
@Repository
public interface PlanoCuidadosRepository extends JpaRepository<PlanoCuidados, Long> {
    
    /**
     * Fetch plano ativo para uma estadia (UNIQUE constraint)
     */
    Optional<PlanoCuidados> findByEstadiaId(Long estadiaId);

    /**
     * Fetch plano ativo e único para estadia
     */
    Optional<PlanoCuidados> findUniqueActiveByEstadiaIdAndAtivoTrue(Long estadiaId);

    /**
     * Histórico de planos do animal (paginado, ordenado por data de início)
     */
    Page<PlanoCuidados> findByAnimalIdOrderByDataInicio(Long animalId, Pageable pageable);

    /**
     * Contar planos ativos do animal
     */
    long countByAnimalIdAndAtivoTrue(Long animalId);
}
