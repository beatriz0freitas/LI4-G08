package pt.hotel.animais.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.hotel.animais.service.IDashboardService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final IDashboardService dashboardService;

    public DashboardController(IDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("alojamentosDisponiveis", dashboardService.contarAlojamentosDisponiveis());
        model.addAttribute("reservasAtivas", dashboardService.contarReservasAtivas());
        model.addAttribute("estadiasEmCurso", dashboardService.contarEstadiasAtivas());
        model.addAttribute("pagamentosPendentes", dashboardService.contarPagamentosPendentes());
        model.addAttribute("faturacaoTotal", dashboardService.faturacaoTotal());
        model.addAttribute("activePage", "dashboard");
        return "dashboard/index";
    }
}
