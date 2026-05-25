package pt.hotel.animais.repository;

























}    long countByPlanoCuidadosId(Long planoCuidadosId);    // Contar todas as tarefas de um plano    long countByPlanoCuidadosIdAndConcluidaTrue(Long planoCuidadosId);    // Contar tarefas concluídas de um plano    List<TarefaCuidado> findByPlanoCuidadosIdAndConcluidaFalse(Long planoCuidadosId);    // Listar tarefas não concluídas de um plano    List<TarefaCuidado> findByPlanoCuidadosId(Long planoCuidadosId);    // Listar todas as tarefas de um planopublic interface TarefaCuidadoRepository extends JpaRepository<TarefaCuidado, Long> {@Repository */ * LAC-02: Suporta queries de tarefas por plano e por estado de conclusão * Repositório para TarefaCuidado./**import java.util.List;import pt.hotel.animais.model.TarefaCuidado;import org.springframework.stereotype.Repository;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.PlanoCuidados;
import java.util.Optional;

/**
 * Repositório para PlanoCuidados.
 * LAC-02: Suporta o duplo vínculo (Animal histórico + Estadia ativa)
 */
@Repository
public interface PlanoCuidadosRepository extends JpaRepository<PlanoCuidados, Long> {
    // Fetch plano ativo para uma estadia
    Optional<PlanoCuidados> findByEstadiaId(Long estadiaId);

    // Fetch plano ativo e único para estadia (UNIQUE constraint)
    Optional<PlanoCuidados> findUniqueActiveByEstadiaIdAndAtivoTrue(Long estadiaId);

    // Histórico de planos do animal (paginado)
    Page<PlanoCuidados> findByAnimalIdOrderByDataInicio(Long animalId, Pageable pageable);

    // Contar planos ativos do animal
    long countByAnimalIdAndAtivoTrue(Long animalId);
}
