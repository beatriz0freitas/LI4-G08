package pt.hotel.animais.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.model.TipoServicoExtra;
import pt.hotel.animais.service.TipoServicoExtraService;

import java.util.List;

/**
 * Controller para gestão do catálogo de tipos de serviços extra.
 * Acesso restrito a ROLE_DIRETOR.
 */
@Controller
@RequestMapping("/admin/tipos-servicos-extra")
@PreAuthorize("hasRole('DIRETOR')")
public class TipoServicoExtraController {

    private final TipoServicoExtraService service;

    @Autowired
    public TipoServicoExtraController(TipoServicoExtraService service) {
        this.service = service;
    }

    /**
     * Listar todos os tipos de serviços (ativos e inativos).
     */
    @GetMapping
    public String listarTipos(Model model) {
        List<TipoServicoExtra> tipos = service.listarTodos();
        model.addAttribute("tipos", tipos);
        model.addAttribute("pageTitle", "Serviços Extra");
        model.addAttribute("activePage", "tipos-servicos-extra");
        return "admin/tipos-servicos-extra/lista";
    }

    /**
     * Formulário de criação de novo tipo.
     */
    @GetMapping("/novo")
    public String formularioCriarTipo(Model model) {
        model.addAttribute("tipo", new TipoServicoExtra());
        model.addAttribute("pageTitle", "Novo Serviço Extra");
        model.addAttribute("activePage", "tipos-servicos-extra");
        return "admin/tipos-servicos-extra/novo";
    }

    /**
     * Criar um novo tipo de serviço extra.
     */
    @PostMapping
    public String criarTipo(
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            RedirectAttributes redirectAttributes) {
        try {
            service.criar(nome, descricao);
            redirectAttributes.addFlashAttribute("mensagem", 
                "Tipo de serviço \"" + nome + "\" criado com sucesso");
            redirectAttributes.addFlashAttribute("tipo", "sucesso");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagem", 
                "Erro ao criar tipo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "erro");
        }
        return "redirect:/admin/tipos-servicos-extra";
    }

    /**
     * Formulário de edição de tipo.
     */
    @GetMapping("/{id}/editar")
    public String formularioEditarTipo(@PathVariable Long id, Model model) {
        TipoServicoExtra tipo = service.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de serviço não encontrado"));
        model.addAttribute("tipo", tipo);
        model.addAttribute("pageTitle", "Editar Serviço Extra");
        model.addAttribute("activePage", "tipos-servicos-extra");
        return "admin/tipos-servicos-extra/editar";
    }

    /**
     * Atualizar um tipo de serviço extra.
     */
    @PostMapping("/{id}")
    public String atualizarTipo(
            @PathVariable Long id,
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            RedirectAttributes redirectAttributes) {
        try {
            service.atualizar(id, nome, descricao);
            redirectAttributes.addFlashAttribute("mensagem", 
                "Tipo de serviço atualizado com sucesso");
            redirectAttributes.addFlashAttribute("tipo", "sucesso");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagem", 
                "Erro ao atualizar tipo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "erro");
        }
        return "redirect:/admin/tipos-servicos-extra";
    }

    /**
     * Desativar um tipo de serviço extra.
     */
    @PostMapping("/{id}/desativar")
    public String desativarTipo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.desativar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Tipo de serviço desativado com sucesso");
            redirectAttributes.addFlashAttribute("tipo", "sucesso");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagem", 
                "Erro ao desativar tipo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "erro");
        }
        return "redirect:/admin/tipos-servicos-extra";
    }

    /**
     * Reativar um tipo de serviço extra.
     */
    @PostMapping("/{id}/reativar")
    public String reativarTipo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.reativar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Tipo de serviço reativado com sucesso");
            redirectAttributes.addFlashAttribute("tipo", "sucesso");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagem", 
                "Erro ao reativar tipo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "erro");
        }
        return "redirect:/admin/tipos-servicos-extra";
    }

    /**
     * API: Obter tipo por ID (JSON).
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> obterTipo(@PathVariable Long id) {
        return service.obterPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * API: Listar apenas tipos ativos (JSON) — para UI dropdowns.
     */
    @GetMapping(value = "/api/ativos", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<TipoServicoExtra>> listarAtivos() {
        return ResponseEntity.ok(service.listarAtivos());
    }
}
