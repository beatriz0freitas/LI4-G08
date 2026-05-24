package pt.hotel.animais.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.service.IPagamentoService;

@Controller
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final IPagamentoService pagamentoService;

    public PagamentoController(IPagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public String registrar(@ModelAttribute PagamentoDto dto, RedirectAttributes redirectAttributes) {
        try {
            pagamentoService.registrarPagamento(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Pagamento registado com sucesso");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/historico";
    }
}
