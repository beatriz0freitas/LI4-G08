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
import pt.hotel.animais.dto.ServicoExtraFormDto;
import pt.hotel.animais.service.ServicoExtraService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/extras")
public class ServicoExtraController {

    private final ServicoExtraService servicoExtraService;

    @GetMapping
    public String list(@RequestParam Long estadiaId, @PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable, Model model) {
        var page = servicoExtraService.listByEstadia(estadiaId, pageable);
        model.addAttribute("extras", page.getContent());
        model.addAttribute("estadiaId", estadiaId);
        model.addAttribute("activePage", "extras");
        return "clinica/extras";
    }

    @PostMapping("/create")
    public String create(ServicoExtraFormDto form, Principal principal, RedirectAttributes redirect) {
        Long autorId = null;
        try { if (principal != null) autorId = Long.parseLong(principal.getName()); } catch (Exception ignored) {}

        try {
            servicoExtraService.register(form, autorId);
            redirect.addFlashAttribute("successMessage", "Serviço extra registado");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/extras?estadiaId=" + form.getEstadiaId();
    }
}
