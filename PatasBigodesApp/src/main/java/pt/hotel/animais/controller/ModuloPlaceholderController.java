package pt.hotel.animais.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class ModuloPlaceholderController {

    private static final Map<String, String> TITULOS = Map.of(
        "estadias", "Check-in / Check-out",
        "cuidados", "Cuidados Diários",
        "clinica", "Clínica Veterinária",
        "pagamentos", "Pagamentos",
        "relatorios", "Relatórios",
        "colaboradores", "Colaboradores"
    );

    @GetMapping({
        "/cuidados",
        "/clinica",
        "/pagamentos",
        "/relatorios",
        "/colaboradores"
    })
    public String placeholder(HttpServletRequest request, Model model) {
        String caminho = request.getRequestURI().replaceFirst("^/", "");
        model.addAttribute("activePage", caminho);
        model.addAttribute("titulo", TITULOS.getOrDefault(caminho, "Módulo"));
        model.addAttribute("descricao", "Este módulo ainda não foi implementado nesta iteração, mas a navegação e a segurança já estão ligadas.");
        return "placeholders/modulo";
    }
}
