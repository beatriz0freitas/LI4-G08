package pt.hotel.animais.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Sort;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.service.IHistoricoService;

import java.time.LocalDate;

@Controller
@RequestMapping("/historico")
public class HistoricoController {

    private final IHistoricoService historicoService;

    public HistoricoController(IHistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping("/eventos")
    public String verEventos(
        @RequestParam(required = false) Long clienteId,
        @RequestParam(required = false) Long animalId,
        @RequestParam(required = false) Long estadiaId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(required = false) String tipoEvento,
        @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable,
        Model model
    ) {
        pt.hotel.animais.dto.HistoricoFiltroDto filtro = new pt.hotel.animais.dto.HistoricoFiltroDto();
        filtro.setClienteId(clienteId);
        filtro.setAnimalId(animalId);
        filtro.setEstadiaId(estadiaId);
        filtro.setDataInicio(dataInicio);
        filtro.setDataFim(dataFim);
        filtro.setTipoEvento(tipoEvento);

        var page = historicoService.consultar(filtro, pageable);

        model.addAttribute("historicoPage", page);
        model.addAttribute("itens", page.getContent());
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("animalId", animalId);
        model.addAttribute("estadiaId", estadiaId);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("tipoEvento", tipoEvento);
        model.addAttribute("pageSize", pageable.getPageSize());
        model.addAttribute("activePage", "historico-eventos");
        model.addAttribute("pageTitle", "Histórico Consolidado");
        return "historico/eventos";
    }

    @GetMapping
    public String verHistorico(
        @RequestParam(required = false) Long clienteId,
        @RequestParam(required = false) Long animalId,
        @RequestParam(required = false) EstadoEstadia estado,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @PageableDefault(size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable,
        Model model
    ) {
        Page<Estadia> historicoPage = historicoService.listarHistorico(clienteId, animalId, estado, dataInicio, dataFim, pageable);

        model.addAttribute("historicoPage", historicoPage);
        model.addAttribute("estadias", historicoPage.getContent());
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("animalId", animalId);
        model.addAttribute("estado", estado);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("pageSize", pageable.getPageSize());
        model.addAttribute("activePage", "historico");
        model.addAttribute("pageTitle", "Histórico de Estadia");
        return "historico/list";
    }
}
