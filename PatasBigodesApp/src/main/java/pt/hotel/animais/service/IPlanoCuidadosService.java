package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.hotel.animais.dto.PlanoCuidadosDto;
import pt.hotel.animais.dto.TarefaCuidadoDto;
import pt.hotel.animais.dto.TarefaCuidadoFormDto;
import pt.hotel.animais.model.enums.PrioridadePlano;
import java.util.List;

/**
 * Interface de serviço para PlanoCuidados.
 */
public interface IPlanoCuidadosService {

    /**
     * Criar um novo plano de cuidados para uma estadia.
     * O plano herda o histórico do animal e cria uma cópia ajustável.
     */
    PlanoCuidadosDto criarPlanoParaEstadia(Long estadiaId, Long animalId) throws Exception;

    /**
     * Obtém ou cria idempotentemente o plano derivado da estadia em curso.
     */
    PlanoCuidadosDto obterOuCriarPlanoParaEstadiaAtiva(Long estadiaId);

    /**
     * Obter o plano ativo para uma estadia.
     */
    PlanoCuidadosDto obterPlanoPorEstadia(Long estadiaId) throws Exception;

    /**
     * Listar planos ativos para selecionar a estadia a acompanhar.
     */
    Page<PlanoCuidadosDto> listarPlanosAtivos(Pageable pageable);

    /**
     * Lista todos os planos de estadias em curso para acompanhamento no turno.
     */
    List<PlanoCuidadosDto> listarPlanosAtivosDoTurno();

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
     * Chamado automaticamente por AlteracaoEstadoSaudeService quando severidade = CRITICO
     */
    void atualizarPrioridade(Long planoCuidadosId, PrioridadePlano novaPrioridade, Long autorId) throws Exception;

    /**
     * Encerrar o plano (chamado no check-out).
     */
    void encerrarPlano(Long planoCuidadosId) throws Exception;

    /**
     * Encerra o plano associado à estadia concluída, quando existir.
     */
    void encerrarPlanoDaEstadia(Long estadiaId);
}
