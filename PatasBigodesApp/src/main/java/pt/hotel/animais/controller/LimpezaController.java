package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.service.ILimpezaService;

@Controller
@RequiredArgsConstructor
public class LimpezaController {
    private final ILimpezaService limpezaService;

    @GetMapping("/limpeza")
    @PreAuthorize("hasAnyRole('DIRETOR', 'RESPONSAVEL_LIMPEZA')")
    public String listarPendentes(Model model) {
        model.addAttribute("activePage", "limpeza");
        model.addAttribute("alojamentosPendentes", limpezaService.listarAlojamentosPendentes());
        return "limpeza/listar";
    }

    @PostMapping("/limpeza/{id}/limpo")
    @PreAuthorize("hasAnyRole('DIRETOR', 'RESPONSAVEL_LIMPEZA')")
    public String marcarComoLimpo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean sucesso = limpezaService.marcarComoLimpo(id);
        if (sucesso) {
            redirectAttributes.addFlashAttribute("msgSucesso", "Alojamento marcado como limpo.");
        } else {
            redirectAttributes.addFlashAttribute("msgErro", "Alojamento não encontrado.");
        }
        return "redirect:/limpeza";
    }
}
