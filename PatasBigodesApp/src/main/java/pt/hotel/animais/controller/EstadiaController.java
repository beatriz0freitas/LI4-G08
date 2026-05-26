package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.service.IEstadiaService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/estadias")
public class EstadiaController {

    private final IEstadiaService estadiaService;

    @GetMapping
    public String operacoes(@RequestParam(required = false) Long reservaId,
                            @RequestParam(required = false) Long estadiaId,
                            Model model) {
        model.addAttribute("activePage", "estadias");
        model.addAttribute("pageTitle", "Check-in / Check-out");
        model.addAttribute("metodosPagamento", pt.hotel.animais.model.enums.MetodoPagamento.values());

        carregarResumoCheckIn(reservaId, model);
        carregarResumoCheckOut(estadiaId, model);

        return "estadias/checkin-checkout";
    }

    private void carregarResumoCheckIn(Long reservaId, Model model) {
        if (reservaId == null) {
            return;
        }

        model.addAttribute("reservaId", reservaId);
        estadiaService.obterResumoCheckIn(reservaId).ifPresentOrElse(resumo -> {
            model.addAttribute("reservaSelecionada", resumo.getReserva());
            model.addAttribute("valorCheckIn", resumo.getValorCheckIn());
        }, () -> model.addAttribute("erroCheckIn", "Reserva não encontrada."));
    }

    private void carregarResumoCheckOut(Long estadiaId, Model model) {
        if (estadiaId == null) {
            return;
        }

        model.addAttribute("estadiaId", estadiaId);
        estadiaService.obterResumoCheckOut(estadiaId).ifPresentOrElse(resumo -> {
            model.addAttribute("estadiaSelecionada", resumo.getEstadia());
            model.addAttribute("valorCheckOut", resumo.getValorCheckOut());
        }, () -> model.addAttribute("erroCheckOut", "Estadia não encontrada."));
    }

    @PostMapping("/check-in")
    public String checkIn(@RequestParam("reservaId") Long reservaId, 
                          @RequestParam(value = "metodoPagamento", required = false) String metodoPagamentoStr,
                          RedirectAttributes redirectAttributes) {
        try {
            pt.hotel.animais.model.enums.MetodoPagamento metodoPagamento = parseMetodoPagamento(metodoPagamentoStr);
            Estadia estadia = estadiaService.abrirEstadiaPorReserva(reservaId, metodoPagamento);
            redirectAttributes.addFlashAttribute("successMessage", "Check-in registado: " + estadia.getId());
            return "redirect:/estadias";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reservas";
        }
    }

    @PostMapping("/check-out")
    public String checkOut(@RequestParam("estadiaId") Long estadiaId,
                           @RequestParam(value = "metodoPagamento", required = false) String metodoPagamentoStr,
                           RedirectAttributes redirectAttributes) {
        try {
            pt.hotel.animais.model.enums.MetodoPagamento metodoPagamento = parseMetodoPagamento(metodoPagamentoStr);
            Estadia estadia = estadiaService.checkOut(estadiaId, metodoPagamento);
            redirectAttributes.addFlashAttribute("successMessage", "Check-out registado: " + estadia.getId());
            return "redirect:/historico";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/historico";
        }
    }

    private pt.hotel.animais.model.enums.MetodoPagamento parseMetodoPagamento(String metodoPagamentoStr) {
        if (metodoPagamentoStr == null || metodoPagamentoStr.isBlank()) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório");
        }
        return pt.hotel.animais.model.enums.MetodoPagamento.valueOf(metodoPagamentoStr);
    }
}
