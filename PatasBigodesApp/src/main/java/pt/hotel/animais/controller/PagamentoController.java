package pt.hotel.animais.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.service.IPagamentoService;

/**
 * Controller MVC para registo manual de pagamentos a partir de formulários.
 *
 * O registo principal de pagamentos também é invocado pelos fluxos de check-in
 * e check-out. Este controller existe para submissões HTML e devolve sempre um
 * redirecionamento com mensagens flash.
 */
@Controller
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final IPagamentoService pagamentoService;

    public PagamentoController(IPagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    /**
     * Apresenta a página de entrada do módulo de pagamentos.
     *
     * @param model modelo MVC usado pelo template Thymeleaf
     * @return template HTML da área de pagamentos
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("activePage", "pagamentos");
        model.addAttribute("pageTitle", "Pagamentos");
        return "pagamentos/index";
    }

    /**
     * Regista um pagamento associado a uma estadia.
     *
     * @param dto dados do pagamento submetidos pelo formulário
     * @param redirectAttributes mensagens flash para o utilizador
     * @return redirecionamento para o histórico operacional/financeiro
     */
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
