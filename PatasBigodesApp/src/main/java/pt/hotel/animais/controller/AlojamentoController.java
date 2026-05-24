package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pt.hotel.animais.service.IAlojamentoService;

@Controller
@RequiredArgsConstructor
public class AlojamentoController {

    private final IAlojamentoService alojamentoService;

    @GetMapping("/alojamentos")
    @PreAuthorize("hasAnyRole('DIRETOR', 'FUNCIONARIO_RECEPCAO', 'RESPONSAVEL_LIMPEZA')")
    public String listar(Model model) {
        model.addAttribute("activePage", "alojamentos");
        model.addAttribute("alojamentos", alojamentoService.listarTodos());
        return "alojamento/listar";
    }
}
