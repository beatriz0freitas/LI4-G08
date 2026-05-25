package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.dto.TarefaCuidadoFormDto;
import pt.hotel.animais.model.enums.PrioridadePlano;
import pt.hotel.animais.service.IPlanoCuidadosService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plano-cuidados")
@Slf4j
public class PlanoCuidadosController {

    private final IPlanoCuidadosService planoCuidadosService;

    /**
     * Visualizar plano de cuidados ativo para uma estadia
     */
    @GetMapping
    public String viewPlano(@RequestParam Long estadiaId, Model model) {
        try {
            var plano = planoCuidadosService.obterPlanoPorEstadia(estadiaId);
            model.addAttribute("plano", plano);
            model.addAttribute("estadiaId", estadiaId);
            model.addAttribute("pageTitle", "Plano de Cuidados");
            model.addAttribute("prioridades", PrioridadePlano.values());
            return "cuidados/plano";
        } catch (Exception e) {
            log.error("Erro ao obter plano para estadia {}: {}", estadiaId, e.getMessage());
            model.addAttribute("error", "Plano de cuidados não encontrado");
            return "redirect:/estadias?error=plano-nao-encontrado";
        }
    }

    /**
     * Criar novo plano de cuidados para uma estadia
     */
    @PostMapping("/criar")
    public String criarPlano(@RequestParam Long estadiaId, @RequestParam Long animalId,
                             RedirectAttributes redirectAttributes) {
        try {
            planoCuidadosService.criarPlanoParaEstadia(estadiaId, animalId);
            redirectAttributes.addFlashAttribute("successMessage", "Plano de cuidados criado com sucesso");
            return "redirect:/plano-cuidados?estadiaId=" + estadiaId;
        } catch (Exception e) {
            log.error("Erro ao criar plano: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar plano: " + e.getMessage());
            return "redirect:/estadias?estadiaId=" + estadiaId;
        }
    }

    /**
     * Adicionar tarefa ao plano
     */
    @PostMapping("/{planoCuidadosId}/tarefa")
    public String adicionarTarefa(@PathVariable Long planoCuidadosId,
                                  @RequestParam String tipo,
                                  @RequestParam String descricao,
                                  @RequestParam String periodicidade,
                                  @RequestParam String dataHora,
                                  @RequestParam Long estadiaId,
                                  RedirectAttributes redirectAttributes) {
        try {
            TarefaCuidadoFormDto formDto = new TarefaCuidadoFormDto();
            formDto.setTipo(tipo);
            formDto.setDescricao(descricao);
            formDto.setPeriodicidade(
                pt.hotel.animais.model.enums.PeriodicidadeTarefa.valueOf(periodicidade)
            );
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            formDto.setDataHora(LocalDateTime.parse(dataHora, formatter));

            Long autorId = 1L; // Obter do SecurityContext em produção
            planoCuidadosService.adicionarTarefa(planoCuidadosId, formDto, autorId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Tarefa adicionada com sucesso");
            return "redirect:/plano-cuidados?estadiaId=" + estadiaId;
        } catch (Exception e) {
            log.error("Erro ao adicionar tarefa: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar tarefa: " + e.getMessage());
            return "redirect:/plano-cuidados?estadiaId=" + estadiaId;
        }
    }

    /**
     * Marcar tarefa como concluída
     */
    @PostMapping("/tarefa/{tarefaId}/concluir")
    public String marcarTarefaConcluida(@PathVariable Long tarefaId,
                                       @RequestParam Long estadiaId,
                                       RedirectAttributes redirectAttributes) {
        try {
            Long autorId = 1L; // Obter do SecurityContext em produção
            planoCuidadosService.marcarTarefaConcluida(tarefaId, autorId);
            redirectAttributes.addFlashAttribute("successMessage", "Tarefa marcada como concluída");
            return "redirect:/plano-cuidados?estadiaId=" + estadiaId;
        } catch (Exception e) {
            log.error("Erro ao marcar tarefa como concluída: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao marcar tarefa: " + e.getMessage());
            return "redirect:/plano-cuidados?estadiaId=" + estadiaId;
        }
    }

    /**
     * Adicionar instruções ao plano
     */
    @PostMapping("/{planoCuidadosId}/instrucoes")
    public String adicionarInstrucoes(@PathVariable Long planoCuidadosId,
                                      @RequestParam String instrucoes,
                                      @RequestParam Long estadiaId,
                                      RedirectAttributes redirectAttributes) {
        try {
            Long autorId = 1L; // Obter do SecurityContext em produção
            planoCuidadosService.adicionarInstrucoes(planoCuidadosId, instrucoes, autorId);
            redirectAttributes.addFlashAttribute("successMessage", "Instruções adicionadas com sucesso");
            return "redirect:/plano-cuidados?estadiaId=" + estadiaId;
        } catch (Exception e) {
            log.error("Erro ao adicionar instruções: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar instruções: " + e.getMessage());
            return "redirect:/plano-cuidados?estadiaId=" + estadiaId;
        }
    }

    /**
     * Atualizar prioridade do plano
     */
    @PostMapping("/{planoCuidadosId}/prioridade")
    public String atualizarPrioridade(@PathVariable Long planoCuidadosId,
                                      @RequestParam String prioridade,
                                      @RequestParam Long estadiaId,
                                      RedirectAttributes redirectAttributes) {
        try {
            Long autorId = 1L; // Obter do SecurityContext em produção
            PrioridadePlano novaPrioridade = PrioridadePlano.valueOf(prioridade);
            planoCuidadosService.atualizarPrioridade(planoCuidadosId, novaPrioridade, autorId);
            redirectAttributes.addFlashAttribute("successMessage", "Prioridade atualizada com sucesso");
            return "redirect:/plano-cuidados?estadiaId=" + estadiaId;
        } catch (Exception e) {
            log.error("Erro ao atualizar prioridade: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar prioridade: " + e.getMessage());
            return "redirect:/plano-cuidados?estadiaId=" + estadiaId;
        }
    }

    /**
     * Listar histórico de planos de um animal
     */
    @GetMapping("/animal/{animalId}/historico")
    public String historicoPlanos(@PathVariable Long animalId,
                                  @RequestParam(defaultValue = "0") int page,
                                  Model model) {
        try {
            Pageable pageable = PageRequest.of(page, 10);
            var planosPage = planoCuidadosService.listarPlanosDoAnimal(animalId, pageable);
            model.addAttribute("planosPage", planosPage);
            model.addAttribute("animalId", animalId);
            model.addAttribute("pageTitle", "Histórico de Planos de Cuidados");
            return "cuidados/historico";
        } catch (Exception e) {
            log.error("Erro ao listar histórico de planos: {}", e.getMessage());
            model.addAttribute("error", "Erro ao listar histórico");
            return "error";
        }
    }

    /**
     * Encerrar plano (chamado no check-out)
     */
    @PostMapping("/{planoCuidadosId}/encerrar")
    public String encerrarPlano(@PathVariable Long planoCuidadosId,
                               @RequestParam Long estadiaId,
                               RedirectAttributes redirectAttributes) {
        try {
            planoCuidadosService.encerrarPlano(planoCuidadosId);
            redirectAttributes.addFlashAttribute("successMessage", "Plano de cuidados encerrado");
            return "redirect:/estadias?estadiaId=" + estadiaId;
        } catch (Exception e) {
            log.error("Erro ao encerrar plano: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao encerrar plano: " + e.getMessage());
            return "redirect:/estadias?estadiaId=" + estadiaId;
        }
    }
}
