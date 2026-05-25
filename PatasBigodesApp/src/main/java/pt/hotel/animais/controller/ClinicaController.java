package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.dto.IntervencaoClinicaFormDto;
import pt.hotel.animais.dto.AlteracaoEstadoSaudeFormDto;
import pt.hotel.animais.model.enums.EstadoSaude;
import pt.hotel.animais.service.IClinicaService;
import pt.hotel.animais.service.IIntervencaoClinicaService;
import pt.hotel.animais.service.IAlteracaoEstadoSaudeService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clinica")
public class ClinicaController {

    private final IIntervencaoClinicaService intervencaoClinicaService;
    private final IAlteracaoEstadoSaudeService alteracaoEstadoSaudeService;
    private final IClinicaService clinicaService;

    /**
     * Apresenta a página de entrada da área clínica.
     *
     * @param model modelo MVC usado pelo template Thymeleaf
     * @return template HTML da área clínica
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("activePage", "clinica");
        model.addAttribute("pageTitle", "Clínica Veterinária");
        model.addAttribute("animais", clinicaService.listarAnimais());
        return "clinica/index";
    }

    @GetMapping("/animais/{animalId}")
    public String detalheAnimal(@PathVariable Long animalId, Model model, RedirectAttributes redirect) {
        try {
            var ficha = clinicaService.obterFichaAnimal(animalId);
            model.addAttribute("activePage", "clinica");
            model.addAttribute("pageTitle", "Ficha Clínica");
            model.addAttribute("estadosSaude", EstadoSaude.values());
            model.addAttribute("intervencaoForm", new IntervencaoClinicaFormDto());
            model.addAttribute("alteracaoForm", new AlteracaoEstadoSaudeFormDto());
            model.addAttribute("animalSelecionado", ficha.getAnimal());
            model.addAttribute("estadiaSelecionada", ficha.getEstadia());
            model.addAttribute("eventosClinicos", ficha.getEventos());
            return "clinica/detalhe";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("errorMessage", "Animal não encontrado");
            return "redirect:/clinica";
        }
    }

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
        return redirectClinicaPorEstadia(form.getEstadiaId());
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
        return redirectClinicaPorEstadia(form.getEstadiaId());
    }

    private String redirectClinicaPorEstadia(Long estadiaId) {
        if (estadiaId == null) {
            return "redirect:/clinica";
        }
        return clinicaService.obterAnimalIdPorEstadia(estadiaId)
            .map(animalId -> "redirect:/clinica/animais/" + animalId)
            .orElse("redirect:/clinica");
    }

}
