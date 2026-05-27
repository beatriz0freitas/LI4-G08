package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import pt.hotel.animais.dto.DisponibilidadeAlojamentoDto;
import pt.hotel.animais.dto.ReservaFormDto;
import pt.hotel.animais.dto.ReservaListDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.EstadoReserva;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.service.IAlojamentoService;
import pt.hotel.animais.service.IAnimalService;
import pt.hotel.animais.service.IReservaService;
import pt.hotel.animais.service.ITutorService;
import pt.hotel.animais.service.TipoAlojamentoPolicy;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller para gerenciar reservas e disponibilidade de alojamentos.
 * Suporta User Stories 3 e 4: Consulta de Disponibilidade e Criação de Reserva.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/reservas")
public class ReservaController {

    private static final List<String> PASSOS_RESERVA = List.of(
        "passo1", "passo2", "passo3", "passo4", "passo5"
    );
    
    private final IReservaService reservaService;
    private final IAlojamentoService alojamentoService;
    private final ITutorService tutorService;
    private final IAnimalService animalService;
    
    /**
     * GET /reservas - Lista de reservas com filtro opcional por estado.
     */
    @GetMapping
    public String listar(
        @RequestParam(required = false) String estado,
        Model model
    ) {
        EstadoReserva filtroEstado = null;
        if (estado != null && !estado.isEmpty()) {
            try {
                filtroEstado = EstadoReserva.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                model.addAttribute("errorMessage", "Estado inválido: " + estado);
            }
        }
        
        List<ReservaListDto> reservas = reservaService.listarComFiltros(filtroEstado);
        
        model.addAttribute("reservas", reservas);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("estados", EstadoReserva.values());
        model.addAttribute("metodosPagamento", MetodoPagamento.values());
        model.addAttribute("pageTitle", "Reservas");
        model.addAttribute("breadcrumb", "Lista de Reservas");
        model.addAttribute("activePage", "reservas");
        
        return "reservas/lista";
    }
    
    /**
     * GET /reservas/disponibilidade - Mapa único de disponibilidade.
     */
    @GetMapping("/disponibilidade")
    public String disponibilidadeForm(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(required = false) String tipo,
        @RequestParam(required = false) Long alojamentoId,
        Model model
    ) {
        LocalDate inicio = dataInicio != null ? dataInicio : LocalDate.now();
        LocalDate fim = dataFim != null ? dataFim : inicio.plusDays(1);
        List<DisponibilidadeAlojamentoDto> mapa = List.of();

        try {
            mapa = alojamentoService.consultarMapaDisponibilidade(inicio, fim, tipo);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        DisponibilidadeAlojamentoDto selecionado = mapa.stream()
            .filter(item -> alojamentoId != null && alojamentoId.equals(item.getAlojamentoId()))
            .findFirst()
            .orElse(mapa.stream().findFirst().orElse(null));
        List<String> tipos = alojamentoService.listarTipos();

        model.addAttribute("mapaAlojamentos", mapa);
        model.addAttribute("alojamentoSelecionado", selecionado);
        model.addAttribute("totalLivres", contarEstado(mapa, "LIVRE"));
        model.addAttribute("totalOcupados", contarEstado(mapa, "OCUPADO"));
        model.addAttribute("totalReservados", contarEstado(mapa, "RESERVADO"));
        model.addAttribute("totalLimpeza", contarEstado(mapa, "LIMPEZA"));
        model.addAttribute("tiposAlojamento", tipos != null ? tipos : List.of());
        model.addAttribute("metodosPagamento", MetodoPagamento.values());
        model.addAttribute("tipo", tipo);
        model.addAttribute("dataInicio", inicio);
        model.addAttribute("dataFim", fim);
        model.addAttribute("pageTitle", "Mapa de Disponibilidade");
        model.addAttribute("breadcrumb", "Mapa de Disponibilidade");
        model.addAttribute("activePage", "disponibilidade");

        return "reservas/disponibilidade";
    }
    
    /**
     * POST /reservas/procurar-disponibilidade - Procura alojamentos disponíveis.
     */
    @PostMapping("/procurar-disponibilidade")
    public String procurarDisponibilidade(
        @RequestParam("dataInicio") LocalDate dataInicio,
        @RequestParam("dataFim") LocalDate dataFim,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        try {
            if (dataInicio == null || dataFim == null) {
                model.addAttribute("errorMessage", "Datas são obrigatórias");
                model.addAttribute("pageTitle", "Consultar Disponibilidade");
                model.addAttribute("activePage", "disponibilidade");
                model.addAttribute("dataInicio", dataInicio);
                model.addAttribute("dataFim", dataFim);
                return "reservas/disponibilidade";
            }
            
            if (!dataInicio.isBefore(dataFim)) {
                model.addAttribute("errorMessage", "Data de início deve ser anterior a data de fim");
                model.addAttribute("pageTitle", "Consultar Disponibilidade");
                model.addAttribute("activePage", "disponibilidade");
                model.addAttribute("dataInicio", dataInicio);
                model.addAttribute("dataFim", dataFim);
                return "reservas/disponibilidade";
            }
            
            List<DisponibilidadeAlojamentoDto> disponibilidades = 
                alojamentoService.consultarDisponibilidade(dataInicio, dataFim);
            
            if (disponibilidades.isEmpty()) {
                model.addAttribute("warningMessage", 
                    "Nenhum alojamento disponível para o período especificado (de " + dataInicio + " a " + dataFim + ")");
            }
            
            model.addAttribute("disponibilidades", disponibilidades);
            model.addAttribute("dataInicio", dataInicio);
            model.addAttribute("dataFim", dataFim);
            model.addAttribute("pageTitle", "Resultados de Disponibilidade");
            model.addAttribute("breadcrumb", "Alojamentos Disponíveis");
            model.addAttribute("activePage", "disponibilidade");
            
            return "reservas/index";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Consultar Disponibilidade");
            model.addAttribute("activePage", "disponibilidade");
            model.addAttribute("dataInicio", dataInicio);
            model.addAttribute("dataFim", dataFim);
            return "reservas/disponibilidade";
        }
    }
    
    /**
     * GET /reservas/novo - Formulário para criar uma nova reserva.
     */
    @GetMapping("/novo")
    public String novoForm(
        @RequestParam(name = "tutorId", required = false) Long tutorId,
        @RequestParam(name = "animalId", required = false) Long animalId,
        @RequestParam(name = "alojamentoId", required = false) Long alojamentoId,
        @RequestParam(name = "dataInicio", required = false) String dataInicio,
        @RequestParam(name = "dataFim", required = false) String dataFim,
        @RequestParam(name = "step", defaultValue = "passo1") String step,
        Model model
    ) {
        ReservaFormDto reservaForm = new ReservaFormDto();
        
        if (tutorId != null) {
            reservaForm.setTutorId(tutorId);
        }
        if (animalId != null) {
            reservaForm.setAnimalId(animalId);
        }
        if (alojamentoId != null) {
            reservaForm.setAlojamentoId(alojamentoId);
        }
        try {
            reservaForm.setDataInicio(parseDate(dataInicio));
            reservaForm.setDataFim(parseDate(dataFim));
        } catch (DateTimeParseException e) {
            model.addAttribute("warningMessage", "Formato de data inválido. Introduza as datas novamente.");
        }
        
        prepararFormularioReserva(reservaForm, model);
        model.addAttribute("activeStep", determinarPassoAtivo(step, reservaForm, model));
        
        return "reservas/form";
    }
    
    /**
     * POST /reservas - Cria uma nova reserva.
     */
    @PostMapping
    public String criar(
        @Valid @ModelAttribute("reservaForm") ReservaFormDto formDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            prepararFormularioReserva(formDto, model);
            model.addAttribute("activeStep", passoComErros(bindingResult));
            return "reservas/form";
        }
        
        try {
            Reserva reserva = reservaService.criar(formDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Reserva criada com sucesso! ID: " + reserva.getId());
            return "redirect:/reservas/" + reserva.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            prepararFormularioReserva(formDto, model);
            model.addAttribute("activeStep", determinarPassoAtivo("passo5", formDto, model));
            return "reservas/form";
        }
    }
    
    /**
     * GET /reservas/{id} - Detalhe de uma reserva com opções de ação.
     */
    @GetMapping("/{id}")
    public String detalhe(
        @PathVariable Long id,
        Model model
    ) {
        try {
            Reserva reserva = reservaService.obter(id);
            
            model.addAttribute("reserva", reserva);
            model.addAttribute("metodosPagamento", MetodoPagamento.values());
            model.addAttribute("pageTitle", "Reserva #" + id);
            model.addAttribute("breadcrumb", "Detalhes da Reserva");
            model.addAttribute("activePage", "reservas");

            reservaService.obterDetalheFinanceiro(id).ifPresent(detalheFinanceiro -> {
                model.addAttribute("extrasTotal", detalheFinanceiro.getExtrasTotal());
                model.addAttribute("estadiaId", detalheFinanceiro.getEstadiaId());
            });
            
            return "reservas/confirmacao";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Reserva não encontrada");
            return "redirect:/reservas";
        }
    }
    
    /**
     * POST /reservas/{id}/cancelar - Cancela uma reserva.
     */
    @PostMapping("/{id}/cancelar")
    public String cancelar(
        @PathVariable Long id,
        @RequestParam(value = "redirectTo", required = false) String redirectTo,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        try {
            reservaService.cancelar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva cancelada com sucesso");
            return "redirect:" + destinoSeguro(redirectTo, "/reservas");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:" + destinoSeguro(redirectTo, "/reservas/" + id);
        }
    }
    
    /**
     * POST /reservas/{id}/concluir - Marca uma reserva como concluída.
     */
    @PostMapping("/{id}/concluir")
    public String concluir(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        redirectAttributes.addFlashAttribute(
            "errorMessage",
            "A reserva só pode ser concluída automaticamente após o check-out da estadia associada"
        );
        return "redirect:/reservas/" + id;
    }

    /**
     * POST /reservas/{id}/confirmar - A confirmação ocorre apenas no check-in.
     */
    @PostMapping("/{id}/confirmar")
    public String confirmar(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        redirectAttributes.addFlashAttribute(
            "errorMessage",
            "A reserva só fica confirmada durante o check-in"
        );
        return "redirect:/reservas/" + id;
    }

    private long contarEstado(List<DisponibilidadeAlojamentoDto> mapa, String estado) {
        return mapa.stream()
            .filter(alojamento -> estado.equals(alojamento.getEstado()))
            .count();
    }

    private void prepararFormularioReserva(ReservaFormDto reservaForm, Model model) {
        List<Tutor> tutores = tutorService.listarTodos();
        List<Animal> animaisTutor = new ArrayList<>();
        List<DisponibilidadeAlojamentoDto> disponibilidades = new ArrayList<>();
        String tipoAlojamentoEsperado = null;

        if (reservaForm.getTutorId() != null && tutores.stream()
            .noneMatch(tutor -> tutor.getId().equals(reservaForm.getTutorId()))) {
            adicionarAvisoSeAusente(model, "Tutor não encontrado. Selecione outro tutor.");
            reservaForm.setTutorId(null);
            reservaForm.setAnimalId(null);
            reservaForm.setAlojamentoId(null);
        }

        if (reservaForm.getTutorId() != null) {
            try {
                animaisTutor = animalService.procurarPorTutor(reservaForm.getTutorId());
            } catch (IllegalArgumentException e) {
                adicionarAvisoSeAusente(model, "Tutor não encontrado. Selecione outro tutor.");
                reservaForm.setTutorId(null);
                reservaForm.setAnimalId(null);
                reservaForm.setAlojamentoId(null);
            }
        }

        if (reservaForm.getAnimalId() != null) {
            try {
                Animal animalSelecionado = animalService.obter(reservaForm.getAnimalId());
                boolean animalPertenceAoTutor = reservaForm.getTutorId() == null
                    || animalSelecionado.getTutor().getId().equals(reservaForm.getTutorId());

                if (!animalPertenceAoTutor) {
                    adicionarAvisoSeAusente(model, "O animal selecionado não pertence ao tutor indicado.");
                    reservaForm.setAnimalId(null);
                    reservaForm.setAlojamentoId(null);
                } else {
                    tipoAlojamentoEsperado = TipoAlojamentoPolicy.fromEspecie(animalSelecionado.getEspecie());
                }
            } catch (IllegalArgumentException e) {
                adicionarAvisoSeAusente(model, "Animal não encontrado. Selecione outro animal.");
                reservaForm.setAnimalId(null);
                reservaForm.setAlojamentoId(null);
            }
        }

        if (tipoAlojamentoEsperado != null
            && reservaForm.getDataInicio() != null
            && reservaForm.getDataFim() != null) {
            String erroPeriodo = validarPeriodo(reservaForm);
            if (erroPeriodo != null) {
                adicionarAvisoSeAusente(model, erroPeriodo);
                reservaForm.setAlojamentoId(null);
            } else {
                try {
                    Animal animalSelecionado = animalService.obter(reservaForm.getAnimalId());
                    disponibilidades = alojamentoService.consultarDisponibilidade(
                        reservaForm.getDataInicio(),
                        reservaForm.getDataFim(),
                        animalSelecionado.getEspecie()
                    );
                } catch (IllegalArgumentException e) {
                    adicionarAvisoSeAusente(model, e.getMessage());
                }
            }
        }

        if (reservaForm.getAlojamentoId() != null
            && tipoAlojamentoEsperado != null
            && validarPeriodo(reservaForm) == null
            && disponibilidades.stream()
            .noneMatch(d -> d.getAlojamentoId().equals(reservaForm.getAlojamentoId()))) {
            adicionarAvisoSeAusente(model, "O alojamento selecionado já não está disponível para estes dados.");
            reservaForm.setAlojamentoId(null);
        }

        model.addAttribute("reservaForm", reservaForm);
        model.addAttribute("tutores", tutores);
        model.addAttribute("disponibilidades", disponibilidades);
        model.addAttribute("animaisTutor", animaisTutor);
        model.addAttribute("tipoAlojamentoEsperado", tipoAlojamentoEsperado);
        model.addAttribute("dataMinima", LocalDate.now());
        model.addAttribute("pageTitle", "Nova Reserva");
        model.addAttribute("breadcrumb", "Criar Reserva");
        model.addAttribute("activePage", "reservas");
    }

    private String determinarPassoAtivo(String step, ReservaFormDto reservaForm, Model model) {
        String passoPedido = PASSOS_RESERVA.contains(step) ? step : "passo1";
        int indice = PASSOS_RESERVA.indexOf(passoPedido);

        if (indice >= 1 && reservaForm.getTutorId() == null) {
            adicionarAvisoSeAusente(model, "Selecione primeiro um tutor.");
            return "passo1";
        }
        if (indice >= 2 && reservaForm.getAnimalId() == null) {
            adicionarAvisoSeAusente(model, "Selecione primeiro um animal.");
            return "passo2";
        }
        if (indice >= 3) {
            String erroPeriodo = validarPeriodo(reservaForm);
            if (erroPeriodo != null) {
                adicionarAvisoSeAusente(model, erroPeriodo);
                return "passo3";
            }
        }
        if (indice >= 4 && reservaForm.getAlojamentoId() == null) {
            adicionarAvisoSeAusente(model, "Selecione um alojamento disponível antes de avançar.");
            return "passo4";
        }

        return passoPedido;
    }

    private String validarPeriodo(ReservaFormDto reservaForm) {
        if (reservaForm.getDataInicio() == null || reservaForm.getDataFim() == null) {
            return "Preencha o período antes de avançar.";
        }
        if (reservaForm.getDataInicio().isBefore(LocalDate.now())) {
            return "A data de início não pode ser anterior à data atual.";
        }
        if (!reservaForm.getDataInicio().isBefore(reservaForm.getDataFim())) {
            return "A data de início deve ser anterior à data de fim.";
        }
        return null;
    }

    private String passoComErros(BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors("tutorId")) {
            return "passo1";
        }
        if (bindingResult.hasFieldErrors("animalId")) {
            return "passo2";
        }
        if (bindingResult.hasFieldErrors("dataInicio") || bindingResult.hasFieldErrors("dataFim")) {
            return "passo3";
        }
        return "passo4";
    }

    private void adicionarAvisoSeAusente(Model model, String mensagem) {
        if (!model.containsAttribute("warningMessage")) {
            model.addAttribute("warningMessage", mensagem);
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private String destinoSeguro(String redirectTo, String fallback) {
        if (redirectTo == null || redirectTo.isBlank() || !redirectTo.startsWith("/") || redirectTo.startsWith("//")) {
            return fallback;
        }
        return redirectTo;
    }
}
