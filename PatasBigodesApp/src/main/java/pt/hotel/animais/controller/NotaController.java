package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.dto.NotaFormDto;
import pt.hotel.animais.service.NotaService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notas")
public class NotaController {

    private final NotaService notaService;

    @PostMapping("/create")
    public String create(NotaFormDto form, Principal principal, RedirectAttributes redirect) {
        Long autorId = null;
        try { if (principal != null) autorId = Long.parseLong(principal.getName()); } catch (Exception ignored) {}

        try {
            notaService.create(form, autorId);
            redirect.addFlashAttribute("successMessage", "Nota adicionada");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/reservas";
    }
}
