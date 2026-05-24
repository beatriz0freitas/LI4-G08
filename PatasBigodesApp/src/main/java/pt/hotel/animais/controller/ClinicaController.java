package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.dto.IntervencaoClinicaFormDto;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeFormDto;
import pt.hotel.animais.service.IIntervencaoClinicaService;
import pt.hotel.animais.service.IAlteracaoEstadoSaudeService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clinica")
public class ClinicaController {

    private final IIntervencaoClinicaService intervencaoClinicaService;
    private final IAlteracaoEstadoSaudeService alteracaoEstadoSaudeService;

    @GetMapping("/intervencoes")
    public String listIntervencoes(@RequestParam Long estadiaId, @PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable, Model model) {
        var page = intervencaoClinicaService.listByEstadia(estadiaId, pageable);
        model.addAttribute("intervencoes", page.getContent());
        model.addAttribute("estadiaId", estadiaId);
        return "clinica/intervencoes";
    }

    @PostMapping("/intervencoes/create")
    public String createIntervencao(IntervencaoClinicaFormDto form, Principal principal, RedirectAttributes redirect) {
        Long autorId = null; try { if (principal != null) autorId = Long.parseLong(principal.getName()); } catch (Exception ignored) {}
        try { intervencaoClinicaService.register(form); redirect.addFlashAttribute("successMessage","Intervenção registada"); }
        catch (Exception e){ redirect.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/clinica/intervencoes?estadiaId="+form.getEstadiaId();
    }

    @GetMapping("/alteracoes")
    public String listAlteracoes(@RequestParam Long estadiaId, @PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable, Model model) {
        var page = alteracaoEstadoSaudeService.listByEstadia(estadiaId, pageable);
        model.addAttribute("alteracoes", page.getContent());
        model.addAttribute("estadiaId", estadiaId);
        return "clinica/alteracoes";
    }

    @PostMapping("/alteracoes/create")
    public String createAlteracao(AlteracaoEstadoSaudeFormDto form, Principal principal, RedirectAttributes redirect) {
        Long autorId = null; try { if (principal != null) autorId = Long.parseLong(principal.getName()); } catch (Exception ignored) {}
        try { alteracaoEstadoSaudeService.register(form); redirect.addFlashAttribute("successMessage","Alteração registada"); }
        catch (Exception e){ redirect.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/clinica/alteracoes?estadiaId="+form.getEstadiaId();
    }
}
