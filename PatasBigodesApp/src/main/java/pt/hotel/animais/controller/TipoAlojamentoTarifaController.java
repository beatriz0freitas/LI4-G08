package pt.hotel.animais.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.model.TipoAlojamentoTarifa;
import pt.hotel.animais.service.TipoAlojamentoTarifaService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller para gestão de tarifas por tipo de alojamento.
 * Acesso restrito a ROLE_DIRETOR.
 */
@Controller
@RequestMapping("/admin/tarifas")
@PreAuthorize("hasRole('DIRETOR')")
public class TipoAlojamentoTarifaController {

    private final TipoAlojamentoTarifaService service;

    @Autowired
    public TipoAlojamentoTarifaController(TipoAlojamentoTarifaService service) {
        this.service = service;
    }

    /**
     * Listar todas as tarifas (ativas e inativas).
     */
    @GetMapping
    public String listarTarifas(Model model) {
        List<TipoAlojamentoTarifa> tarifas = service.listarTodas();
        model.addAttribute("tarifas", tarifas);
        model.addAttribute("pageTitle", "Tipos de Alojamento e Tarifas");
        model.addAttribute("activePage", "tarifas");
        return "admin/tarifas/lista";
    }

    /**
     * Formulário de criação de nova tarifa.
     */
    @GetMapping("/nova")
    public String formularioCriarTarifa(Model model) {
        model.addAttribute("tarifa", new TipoAlojamentoTarifa());
        model.addAttribute("pageTitle", "Novo Tipo de Alojamento");
        model.addAttribute("activePage", "tarifas");
        return "admin/tarifas/nova";
    }

    /**
     * Criar uma nova tarifa.
     */
    @PostMapping
    public String criarTarifa(
            @RequestParam String tipoAlojamento,
            @RequestParam BigDecimal tarifaDiaria,
            RedirectAttributes redirectAttributes) {
        try {
            service.criar(tipoAlojamento, tarifaDiaria);
            redirectAttributes.addFlashAttribute("mensagem", 
                "Tarifa para " + tipoAlojamento + " criada com sucesso: €" + tarifaDiaria);
            redirectAttributes.addFlashAttribute("tipo", "sucesso");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagem", 
                "Erro ao criar tarifa: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "erro");
        }
        return "redirect:/admin/tarifas";
    }

    /**
     * Formulário de edição de tarifa.
     */
    @GetMapping("/{id}/editar")
    public String formularioEditarTarifa(@PathVariable Long id, Model model) {
        TipoAlojamentoTarifa tarifa = service.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa não encontrada"));
        model.addAttribute("tarifa", tarifa);
        model.addAttribute("pageTitle", "Editar Tarifa");
        model.addAttribute("activePage", "tarifas");
        return "admin/tarifas/editar";
    }

    /**
     * Atualizar tarifa.
     */
    @PostMapping("/{id}")
    public String atualizarTarifa(
            @PathVariable Long id,
            @RequestParam BigDecimal tarifaDiaria,
            RedirectAttributes redirectAttributes) {
        try {
            service.atualizarTarifa(id, tarifaDiaria);
            redirectAttributes.addFlashAttribute("mensagem", 
                "Tarifa atualizada com sucesso para €" + tarifaDiaria);
            redirectAttributes.addFlashAttribute("tipo", "sucesso");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagem", 
                "Erro ao atualizar tarifa: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "erro");
        }
        return "redirect:/admin/tarifas";
    }

    /**
     * Desativar uma tarifa.
     */
    @PostMapping("/{id}/desativar")
    public String desativarTarifa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.desativar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Tarifa desativada com sucesso");
            redirectAttributes.addFlashAttribute("tipo", "sucesso");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagem", 
                "Erro ao desativar tarifa: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "erro");
        }
        return "redirect:/admin/tarifas";
    }

    /**
     * Reativar uma tarifa.
     */
    @PostMapping("/{id}/reativar")
    public String reativarTarifa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.reativar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Tarifa reativada com sucesso");
            redirectAttributes.addFlashAttribute("tipo", "sucesso");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagem", 
                "Erro ao reativar tarifa: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "erro");
        }
        return "redirect:/admin/tarifas";
    }

    /**
     * API: Obter tarifa por ID (JSON).
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> obterTarifa(@PathVariable Long id) {
        return service.obterPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * API: Listar todas as tarifas ativas (JSON).
     */
    @GetMapping(value = "/api/ativas", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<TipoAlojamentoTarifa>> listarAtivas() {
        return ResponseEntity.ok(service.listarAtivas());
    }
}
