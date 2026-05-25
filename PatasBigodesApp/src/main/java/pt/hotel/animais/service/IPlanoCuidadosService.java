package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.hotel.animais.dto.PlanoCuidadosDto;
import pt.hotel.animais.dto.TarefaCuidadoDto;
import pt.hotel.animais.dto.TarefaCuidadoFormDto;
import pt.hotel.animais.model.enums.PrioridadePlano;

/**
 * Interface de serviço para PlanoCuidados.
 * LAC-02: Gerencia o plano dinâmico de cuidados com 8 operações principais
 */
public interface IPlanoCuidadosService {

    /**
     * Criar um novo plano de cuidados para uma estadia.
     * O plano herda o histórico do animal e cria uma cópia ajustável.
     */
    PlanoCuidadosDto criarPlanoParaEstadia(Long estadiaId, Long animalId) throws Exception;

    /**
     * Obter o plano ativo para uma estadia.
     */
    PlanoCuidadosDto obterPlanoPorEstadia(Long estadiaId) throws Exception;

    /**
     * Listar o histórico de planos de um animal (paginado).
     */
    Page<PlanoCuidadosDto> listarPlanosDoAnimal(Long animalId, Pageable pageable) throws Exception;

    /**
     * Adicionar uma tarefa estruturada ao plano.
     */
    TarefaCuidadoDto adicionarTarefa(Long planoCuidadosId, TarefaCuidadoFormDto formDto, Long autorId) throws Exception;

    /**
     * Marcar uma tarefa como concluída.
     */
    void marcarTarefaConcluida(Long tarefaId, Long autorId) throws Exception;

    /**
     * Adicionar instruções/notas adicionais ao plano.
     */
    void adicionarInstrucoes(Long planoCuidadosId, String instrucoes, Long autorId) throws Exception;

    /**
     * Atualizar a prioridade do plano.
     * LAC-02: Chamado automaticamente por AlteracaoEstadoSaudeService quando severidade = CRITICO
     */
    void atualizarPrioridade(Long planoCuidadosId, PrioridadePlano novaPrioridade, Long autorId) throws Exception;

    /**
     * Encerrar o plano (chamado no check-out).
     */
    void encerrarPlano(Long planoCuidadosId) throws Exception;
}
