package pt.hotel.animais.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.hotel.animais.service.DashboardService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("alojamentosDisponiveis", dashboardService.contarAlojamentosDisponiveis());
        model.addAttribute("reservasAtivas", dashboardService.contarReservasAtivas());
        model.addAttribute("estadiasEmCurso", dashboardService.contarEstadiasAtivas());
        model.addAttribute("pagamentosPendentes", dashboardService.contarPagamentosPendentes());
        return "dashboard/index";
    }
}
