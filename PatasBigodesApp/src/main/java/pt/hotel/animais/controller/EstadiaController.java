package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.dto.EstadiaListDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;
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
