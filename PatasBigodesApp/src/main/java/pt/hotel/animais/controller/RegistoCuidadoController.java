package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.dto.RegistoCuidadoFormDto;
import pt.hotel.animais.service.IRegistoCuidadoService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cuidados")
public class RegistoCuidadoController {

    private final IRegistoCuidadoService registoCuidadoService;

    @GetMapping
    public String list(@RequestParam Long estadiaId, @PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable, Model model) {
        var page = registoCuidadoService.listByEstadia(estadiaId, pageable);
        model.addAttribute("registos", page.getContent());
        model.addAttribute("estadiaId", estadiaId);
        model.addAttribute("activePage", "cuidados");
        return "cuidados/registos";
    }

    @PostMapping("/create")
    public String create(RegistoCuidadoFormDto form, Principal principal, RedirectAttributes redirect) {
        Long autorId = null;
        try {
            if (principal != null) autorId = Long.parseLong(principal.getName());
        } catch (Exception ignored) { }

        try {
            registoCuidadoService.create(form, autorId);
            redirect.addFlashAttribute("successMessage", "Registo de cuidado criado");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cuidados?estadiaId=" + form.getEstadiaId();
    }
}
