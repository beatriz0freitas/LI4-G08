package pt.hotel.animais.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.auditoria.AuditoriaEvento;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório de persistência para eventos de auditoria.
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaEvento, Long>, JpaSpecificationExecutor<AuditoriaEvento> {

    @Override
    @EntityGraph(attributePaths = "utilizador")
    Page<AuditoriaEvento> findAll(@Nullable Specification<AuditoriaEvento> specification, Pageable pageable);

    Page<AuditoriaEvento> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    Page<AuditoriaEvento> findByUtilizador_IdAndTimestampBetween(Long utilizadorId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    Page<AuditoriaEvento> findByOperacaoContainingIgnoreCaseAndTimestampBetween(String operacao, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    Page<AuditoriaEvento> findByEntidadeContainingIgnoreCaseAndTimestampBetween(String entidade, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    List<AuditoriaEvento> findByTimestampBefore(LocalDateTime dataLimite);

    long deleteByTimestampBefore(LocalDateTime dataLimite);
}
