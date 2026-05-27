package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.dto.EstadiaListDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.service.IEstadiaService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/estadias")
public class EstadiaController {

    private final IEstadiaService estadiaService;

    /**
     * GET /estadias - Redireciona para lista de estadias.
     */
    @GetMapping
    public String listarEstadias() {
        return "redirect:/estadias/lista";
    }

    /**
     * GET /estadias/lista - Lista estadias com filtros.
     */
    @GetMapping("/lista")
    public String listar(
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        Model model
    ) {
        EstadoEstadia filtroEstado = null;
        if (estado != null && !estado.isEmpty()) {
            try {
                filtroEstado = EstadoEstadia.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                model.addAttribute("errorMessage", "Estado inválido: " + estado);
            }
        }
        
        List<EstadiaListDto> estadias = estadiaService.listarComFiltros(filtroEstado, dataInicio, dataFim);
        
        model.addAttribute("estadias", estadias);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("estados", EstadoEstadia.values());
        model.addAttribute("pageTitle", "Estadias");
        model.addAttribute("breadcrumb", "Lista de Estadias");
        model.addAttribute("activePage", "estadias");
        
        return "estadias/lista";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return estadiaService.obterComDetalhes(id)
            .map(estadia -> {
                model.addAttribute("estadia", estadia);
                model.addAttribute("pageTitle", "Estadia #" + id);
                model.addAttribute("breadcrumb", "Detalhes da Estadia");
                model.addAttribute("activePage", "estadias");
                return "estadias/detalhe";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Estadia não encontrada.");
                return "redirect:/estadias/lista";
            });
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

    @GetMapping("/check-in")
    public String paginaCheckIn(@RequestParam("reservaId") Long reservaId,
                                @RequestParam(value = "redirectTo", required = false) String redirectTo,
                                Model model) {
        carregarResumoCheckIn(reservaId, model);
        model.addAttribute("metodosPagamento", MetodoPagamento.values());
        model.addAttribute("redirectTo", destinoSeguro(redirectTo, "/reservas"));
        model.addAttribute("voltarPara", destinoSeguro(redirectTo, "/reservas"));
        model.addAttribute("pageTitle", "Registar Check-in");
        model.addAttribute("breadcrumb", "Check-in");
        model.addAttribute("activePage", "reservas");
        return "estadias/check-in";
    }

    @GetMapping("/check-out")
    public String paginaCheckOut(@RequestParam("estadiaId") Long estadiaId,
                                 @RequestParam(value = "redirectTo", required = false) String redirectTo,
                                 Model model) {
        carregarResumoCheckOut(estadiaId, model);
        model.addAttribute("metodosPagamento", MetodoPagamento.values());
        model.addAttribute("redirectTo", destinoSeguro(redirectTo, "/estadias/lista"));
        model.addAttribute("voltarPara", destinoSeguro(redirectTo, "/estadias/lista"));
        model.addAttribute("pageTitle", "Registar Check-out");
        model.addAttribute("breadcrumb", "Check-out");
        model.addAttribute("activePage", "estadias");
        return "estadias/check-out";
    }

    @PostMapping("/check-in")
    public String checkIn(@RequestParam("reservaId") Long reservaId, 
                          @RequestParam(value = "metodoPagamento", required = false) String metodoPagamentoStr,
                          @RequestParam(value = "redirectTo", required = false) String redirectTo,
                          RedirectAttributes redirectAttributes) {
        try {
            MetodoPagamento metodoPagamento = parseMetodoPagamento(metodoPagamentoStr);
            Estadia estadia = estadiaService.abrirEstadiaPorReserva(reservaId, metodoPagamento);
            redirectAttributes.addFlashAttribute("successMessage", "Check-in registado: " + estadia.getId());
            return "redirect:" + destinoSeguro(redirectTo, "/estadias");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:" + destinoSeguro(redirectTo, "/reservas");
        }
    }

    @PostMapping("/check-out")
    public String checkOut(@RequestParam("estadiaId") Long estadiaId,
                           @RequestParam(value = "metodoPagamento", required = false) String metodoPagamentoStr,
                           @RequestParam(value = "redirectTo", required = false) String redirectTo,
                           RedirectAttributes redirectAttributes) {
        try {
            MetodoPagamento metodoPagamento = parseMetodoPagamento(metodoPagamentoStr);
            Estadia estadia = estadiaService.checkOut(estadiaId, metodoPagamento);
            redirectAttributes.addFlashAttribute("successMessage", "Check-out registado: " + estadia.getId());
            return "redirect:" + destinoSeguro(redirectTo, "/historico");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:" + destinoSeguro(redirectTo, "/historico");
        }
    }

    private MetodoPagamento parseMetodoPagamento(String metodoPagamentoStr) {
        if (metodoPagamentoStr == null || metodoPagamentoStr.isBlank()) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório");
        }
        try {
            return MetodoPagamento.valueOf(metodoPagamentoStr);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Método de pagamento inválido");
        }
    }

    private String destinoSeguro(String redirectTo, String fallback) {
        if (redirectTo == null || redirectTo.isBlank() || !redirectTo.startsWith("/") || redirectTo.startsWith("//")) {
            return fallback;
        }
        return redirectTo;
    }
}
