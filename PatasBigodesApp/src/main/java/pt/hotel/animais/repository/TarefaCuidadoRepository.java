package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.TarefaCuidado;

import java.util.List;

/**
 * Repositório para TarefaCuidado.
 * LAC-02: Suporta queries de tarefas por plano e por estado de conclusão
 */
@Repository
public interface TarefaCuidadoRepository extends JpaRepository<TarefaCuidado, Long> {
    
    /**
     * Listar todas as tarefas de um plano
     */
    List<TarefaCuidado> findByPlanoCuidadosId(Long planoCuidadosId);

    /**
     * Listar tarefas não concluídas de um plano
     */
    List<TarefaCuidado> findByPlanoCuidadosIdAndConcluidaFalse(Long planoCuidadosId);

    /**
     * Contar tarefas concluídas de um plano
     */
    long countByPlanoCuidadosIdAndConcluidaTrue(Long planoCuidadosId);

    /**
     * Contar todas as tarefas de um plano
     */
    long countByPlanoCuidadosId(Long planoCuidadosId);
}
