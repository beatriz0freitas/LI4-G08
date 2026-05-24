package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.hotel.animais.service.PlanoCuidadosService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plano-cuidados")
public class PlanoCuidadosController {

    private final PlanoCuidadosService planoCuidadosService;

    @GetMapping
    public String viewPlano(@RequestParam Long estadiaId, Model model) {
        var plano = planoCuidadosService.getPlanoByEstadia(estadiaId);
        model.addAttribute("plano", plano);
        model.addAttribute("estadiaId", estadiaId);
        model.addAttribute("pageTitle", "Plano de Cuidados");
        return "cuidados/plano";
    }
}
