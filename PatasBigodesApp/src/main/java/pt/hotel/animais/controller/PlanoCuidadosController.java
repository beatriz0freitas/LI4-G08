package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.hotel.animais.service.IPlanoCuidadosService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plano-cuidados")
@Slf4j
public class PlanoCuidadosController {

    private final IPlanoCuidadosService planoCuidadosService;

    @GetMapping
    public String viewPlano(@RequestParam Long estadiaId, Model model) {
        try {
            var plano = planoCuidadosService.obterPlanoPorEstadia(estadiaId);
            model.addAttribute("plano", plano);
            model.addAttribute("estadiaId", estadiaId);
            model.addAttribute("pageTitle", "Plano de Cuidados");
            return "cuidados/plano";
        } catch (Exception e) {
            log.error("Erro ao obter plano para estadia {}: {}", estadiaId, e.getMessage());
            model.addAttribute("error", "Plano de cuidados não encontrado");
            return "redirect:/estadias?error=plano-nao-encontrado";
        }
    }
}
