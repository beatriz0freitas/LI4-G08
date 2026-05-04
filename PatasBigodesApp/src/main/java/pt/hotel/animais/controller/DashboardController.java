package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pt.hotel.animais.service.AlojamentoService;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AlojamentoService alojamentoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("alojamentosDisponiveis", alojamentoService.contarAlojamentosDisponiveisDemo());
        model.addAttribute("reservasAtivas", 0);
        model.addAttribute("estadiasEmCurso", 0);
        model.addAttribute("pagamentosPendentes", 0);
        return "dashboard/index";
    }
}
