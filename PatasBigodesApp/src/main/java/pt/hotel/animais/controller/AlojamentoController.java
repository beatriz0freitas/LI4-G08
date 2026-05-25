package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pt.hotel.animais.service.IAlojamentoService;

@Controller
@RequiredArgsConstructor
public class AlojamentoController {

    private final IAlojamentoService alojamentoService;

    @GetMapping("/alojamentos")
    @PreAuthorize("hasAnyRole('DIRETOR', 'FUNCIONARIO_RECEPCAO')")
    public String listar(Model model) {
        model.addAttribute("activePage", "alojamentos");
        model.addAttribute("alojamentos", alojamentoService.listarTodos());
        model.addAttribute("alojamentosComReservasAtivas", alojamentoService.contarAlojamentosComReservasAtivas());
        return "alojamento/listar";
    }

    @GetMapping("/alojamentos/{id}")
    @PreAuthorize("hasAnyRole('DIRETOR', 'FUNCIONARIO_RECEPCAO')")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("activePage", "alojamentos");
        model.addAttribute("alojamento", alojamentoService.obter(id));
        return "alojamento/detalhe";
    }
}
