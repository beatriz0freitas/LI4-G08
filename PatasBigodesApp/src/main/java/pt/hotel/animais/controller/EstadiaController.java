package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.service.EstadiaService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/estadias")
public class EstadiaController {

    private final EstadiaService estadiaService;

    @GetMapping
    public String operacoes(Model model) {
        model.addAttribute("activePage", "estadias");
        model.addAttribute("pageTitle", "Check-in / Check-out");
        return "estadias/checkin-checkout";
    }

    @PostMapping("/check-in")
    public String checkIn(@RequestParam("reservaId") Long reservaId, RedirectAttributes redirectAttributes) {
        try {
            Estadia estadia = estadiaService.abrirEstadiaPorReserva(reservaId);
            redirectAttributes.addFlashAttribute("successMessage", "Check-in registado: " + estadia.getId());
            return "redirect:/estadias";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reservas";
        }
    }

    @PostMapping("/check-out")
    public String checkOut(@RequestParam("estadiaId") Long estadiaId, RedirectAttributes redirectAttributes) {
        try {
            Estadia estadia = estadiaService.checkOut(estadiaId);
            redirectAttributes.addFlashAttribute("successMessage", "Check-out registado: " + estadia.getId());
            return "redirect:/historico";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/historico";
        }
    }
}
