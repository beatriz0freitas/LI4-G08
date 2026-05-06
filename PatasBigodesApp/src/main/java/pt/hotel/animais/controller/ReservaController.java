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
import pt.hotel.animais.model.enums.TipoAlojamento;
import pt.hotel.animais.service.AlojamentoService;
import pt.hotel.animais.service.AnimalService;
import pt.hotel.animais.service.ReservaService;
import pt.hotel.animais.service.TutorService;

import java.time.LocalDate;
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
    
    private final ReservaService reservaService;
    private final AlojamentoService alojamentoService;
    private final TutorService tutorService;
    private final AnimalService animalService;
    
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
     * POST /reservas/buscar-disponibilidade - Busca alojamentos disponíveis.
     */
    @PostMapping("/buscar-disponibilidade")
    public String buscarDisponibilidade(
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
                return "reservas/disponibilidade";
            }
            
            if (!dataInicio.isBefore(dataFim)) {
                model.addAttribute("errorMessage", "Data de início deve ser anterior a data de fim");
                model.addAttribute("pageTitle", "Consultar Disponibilidade");
                model.addAttribute("activePage", "reservas");
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
        @RequestParam(name = "step", required = false, defaultValue = "passo1") String step,
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
        if (dataInicio != null) {
            reservaForm.setDataInicio(java.time.LocalDate.parse(dataInicio));
        }
        if (dataFim != null) {
            reservaForm.setDataFim(java.time.LocalDate.parse(dataFim));
        }
        
        prepararFormularioReserva(reservaForm, model, step);
        
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
            prepararFormularioReserva(formDto, model, "passo4");
            return "reservas/form";
        }
        
        try {
            Reserva reserva = reservaService.criar(formDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Reserva criada com sucesso! ID: " + reserva.getId());
            return "redirect:/reservas/" + reserva.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            prepararFormularioReserva(formDto, model, "passo4");
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
        try {
            reservaService.concluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva concluída com sucesso");
            return "redirect:/reservas";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/reservas/" + id;
        }
    }

    private void prepararFormularioReserva(ReservaFormDto reservaForm, Model model, String activeStep) {
        List<Tutor> tutores = tutorService.listarTodos();
        List<Animal> animaisTutor = new ArrayList<>();
        List<DisponibilidadeAlojamentoDto> disponibilidades = new ArrayList<>();
        TipoAlojamento tipoAlojamentoEsperado = null;

        if (reservaForm.getTutorId() != null) {
            animaisTutor = animalService.procurarPorTutor(reservaForm.getTutorId());
        }

        if (reservaForm.getAnimalId() != null) {
            Animal animalSelecionado = animalService.obter(reservaForm.getAnimalId());
            tipoAlojamentoEsperado = TipoAlojamento.fromEspecie(animalSelecionado.getEspecie());

            if (reservaForm.getDataInicio() != null && reservaForm.getDataFim() != null) {
                try {
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

        model.addAttribute("reservaForm", reservaForm);
        model.addAttribute("tutores", tutores);
        model.addAttribute("disponibilidades", disponibilidades);
        model.addAttribute("animaisTutor", animaisTutor);
        model.addAttribute("tipoAlojamentoEsperado", tipoAlojamentoEsperado);
        model.addAttribute("activeStep", activeStep);
        model.addAttribute("pageTitle", "Nova Reserva");
        model.addAttribute("breadcrumb", "Criar Reserva");
        model.addAttribute("activePage", "reservas");
    }
}
