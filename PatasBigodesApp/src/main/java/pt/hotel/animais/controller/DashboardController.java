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
        model.addAttribute("alojamentosTotal", dashboardService.contarAlojamentosTotal());
        model.addAttribute("alojamentosOcupados", dashboardService.contarAlojamentosOcupados());
        model.addAttribute("alojamentosPendentesLimpeza", dashboardService.contarAlojamentosPendentesLimpeza());
        model.addAttribute("reservasAtivas", dashboardService.contarReservasAtivas());
        model.addAttribute("reservasFuturas", dashboardService.contarReservasFuturas());
        model.addAttribute("estadiasEmCurso", dashboardService.contarEstadiasAtivas());
        model.addAttribute("pagamentosPendentes", dashboardService.contarPagamentosPendentes());
        model.addAttribute("faturacaoTotal", dashboardService.faturacaoTotal());
        model.addAttribute("taxaOcupacao", dashboardService.taxaOcupacao());
        model.addAttribute("faturacaoMes", dashboardService.faturacaoMesAtual());
        model.addAttribute("estadiasDashboard", dashboardService.listarEstadiasEmCurso());
        model.addAttribute("activePage", "dashboard");
        return "dashboard/index";
    }
}
