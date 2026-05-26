package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import pt.hotel.animais.dto.DisponibilidadeAlojamentoDto;
import pt.hotel.animais.dto.ReservaFormDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.Tutor;
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
    
    private final IReservaService reservaService;
    private final IAlojamentoService alojamentoService;
    private final ITutorService tutorService;
    private final IAnimalService animalService;
    private final pt.hotel.animais.repository.EstadiaRepository estadiaRepository;
    private final pt.hotel.animais.service.IPagamentoService pagamentoService;
    
    /**
     * GET /reservas - Lista de reservas (geralmente com filtros).
     */
    @GetMapping
    public String listar(Model model) {
        List<Reserva> reservas = reservaService.listarTodas();
        
        model.addAttribute("reservas", reservas);
        model.addAttribute("pageTitle", "Reservas");
        model.addAttribute("breadcrumb", "Lista de Reservas");
        model.addAttribute("activePage", "reservas");
        
        return "reservas/index";
    }
    
    /**
     * GET /reservas/disponibilidade - Página de consulta de disponibilidade.
     */
    @GetMapping("/disponibilidade")
    public String disponibilidadeForm(Model model) {
        model.addAttribute("pageTitle", "Consultar Disponibilidade");
        model.addAttribute("breadcrumb", "Alojamentos Disponíveis");
        model.addAttribute("activePage", "reservas");
        
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
                model.addAttribute("activePage", "reservas");
                model.addAttribute("dataInicio", dataInicio);
                model.addAttribute("dataFim", dataFim);
                return "reservas/disponibilidade";
            }
            
            if (!dataInicio.isBefore(dataFim)) {
                model.addAttribute("errorMessage", "Data de início deve ser anterior a data de fim");
                model.addAttribute("pageTitle", "Consultar Disponibilidade");
                model.addAttribute("activePage", "reservas");
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
            model.addAttribute("activePage", "reservas");
            
            return "reservas/index";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Consultar Disponibilidade");
            model.addAttribute("activePage", "reservas");
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
            model.addAttribute("pageTitle", "Reserva #" + id);
            model.addAttribute("breadcrumb", "Detalhes da Reserva");
            model.addAttribute("activePage", "reservas");

            // mostrar extras se existir estadia associada
            estadiaRepository.findByReservaId(id).ifPresent(estadia -> {
                try {
                    var extras = pagamentoService.calcularCobrancaComplementar(estadia);
                    model.addAttribute("extrasTotal", extras);
                    model.addAttribute("estadiaId", estadia.getId());
                } catch (Exception ignored) {}
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
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        try {
            reservaService.cancelar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva cancelada com sucesso");
            return "redirect:/reservas";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/reservas/" + id;
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

    private void prepararFormularioReserva(ReservaFormDto reservaForm, Model model) {
        List<Tutor> tutores = tutorService.listarTodos();
        List<Animal> animaisTutor = new ArrayList<>();
        List<DisponibilidadeAlojamentoDto> disponibilidades = new ArrayList<>();
        String tipoAlojamentoEsperado = null;

        if (reservaForm.getTutorId() != null) {
            try {
                animaisTutor = animalService.procurarPorTutor(reservaForm.getTutorId());
            } catch (IllegalArgumentException e) {
                model.addAttribute("warningMessage", "Tutor não encontrado. Selecione outro tutor.");
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
                    model.addAttribute("warningMessage", "O animal selecionado não pertence ao tutor indicado.");
                    reservaForm.setAnimalId(null);
                    reservaForm.setAlojamentoId(null);
                } else {
                    tipoAlojamentoEsperado = TipoAlojamentoPolicy.fromEspecie(animalSelecionado.getEspecie());
                }
            } catch (IllegalArgumentException e) {
                model.addAttribute("warningMessage", "Animal não encontrado. Selecione outro animal.");
                reservaForm.setAnimalId(null);
                reservaForm.setAlojamentoId(null);
            }
        }

        if (tipoAlojamentoEsperado != null
            && reservaForm.getDataInicio() != null
            && reservaForm.getDataFim() != null) {
            if (!reservaForm.getDataInicio().isBefore(reservaForm.getDataFim())) {
                model.addAttribute("warningMessage", "Data de início deve ser anterior à data de fim.");
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
                    model.addAttribute("warningMessage", e.getMessage());
                }
            }
        }

        if (reservaForm.getAlojamentoId() != null && disponibilidades.stream()
            .noneMatch(d -> d.getAlojamentoId().equals(reservaForm.getAlojamentoId()))) {
            model.addAttribute("warningMessage", "O alojamento selecionado já não está disponível para estes dados.");
            reservaForm.setAlojamentoId(null);
        }

        model.addAttribute("reservaForm", reservaForm);
        model.addAttribute("tutores", tutores);
        model.addAttribute("disponibilidades", disponibilidades);
        model.addAttribute("animaisTutor", animaisTutor);
        model.addAttribute("tipoAlojamentoEsperado", tipoAlojamentoEsperado);
        model.addAttribute("pageTitle", "Nova Reserva");
        model.addAttribute("breadcrumb", "Criar Reserva");
        model.addAttribute("activePage", "reservas");
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }
}
