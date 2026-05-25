package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import pt.hotel.animais.dto.AnimalFormDto;
import pt.hotel.animais.dto.TutorFormDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.service.IAnimalService;
import pt.hotel.animais.service.ITutorService;

import java.util.List;

/**
 * Controller para gerenciar tutores e animais.
 * Suporta User Stories 1 e 2: Registo de Tutor e Registo de Animal.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/tutores")
public class TutorAnimalController {
    
    private final ITutorService tutorService;
    private final IAnimalService animalService;
    
    // ==================== TUTOR ENDPOINTS ====================
    
    /**
     * GET /tutores - Lista de tutores com procura opcional.
     * Suporta procura por nome ou NIF.
     */
    @GetMapping
    public String listar(
        @RequestParam(name = "search", required = false) String search,
        Model model
    ) {
        List<Tutor> tutores;
        
        if (search != null && !search.trim().isEmpty()) {
            // Tenta procurar por NIF ou nome
            if (search.matches("\\d+")) {
                // Se é só números, procura por NIF
                try {
                    Tutor tutor = tutorService.procurarPorNif(search);
                    tutores = List.of(tutor);
                } catch (IllegalArgumentException e) {
                    tutores = List.of();
                }
            } else {
                // Procura por nome
                tutores = tutorService.procurarPorNome(search);
            }
            model.addAttribute("search", search);
        } else {
            tutores = tutorService.listarTodos();
        }
        
        model.addAttribute("tutores", tutores);
        model.addAttribute("pageTitle", "Tutores");
        model.addAttribute("breadcrumb", "Lista de Tutores");
        model.addAttribute("activePage", "tutores");
        
        return "tutores/list";
    }
    
    /**
     * GET /tutores/novo - Formulário para criar um novo tutor.
     */
    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("tutorForm", new TutorFormDto());
        model.addAttribute("pageTitle", "Novo Tutor");
        model.addAttribute("breadcrumb", "Registar Tutor");
        model.addAttribute("activePage", "tutores");
        
        return "tutores/form";
    }
    
    /**
     * POST /tutores - Cria um novo tutor.
     * Valida o formulário e trata erros de duplicação de NIF.
     */
    @PostMapping
    public String criar(
        @Valid @ModelAttribute("tutorForm") TutorFormDto formDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Novo Tutor");
            model.addAttribute("breadcrumb", "Registar Tutor");
            model.addAttribute("activePage", "tutores");
            return "tutores/form";
        }
        
        try {
            Tutor tutor = tutorService.registar(formDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Tutor '" + tutor.getNome() + "' registado com sucesso!");
            return "redirect:/tutores/" + tutor.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Novo Tutor");
            model.addAttribute("breadcrumb", "Registar Tutor");
            model.addAttribute("activePage", "tutores");
            return "tutores/form";
        }
    }
    
    /**
     * GET /tutores/{id} - Detalhe de um tutor com lista de animais e reservas.
     */
    @GetMapping("/{id}")
    public String detalhe(
        @PathVariable Long id,
        Model model
    ) {
        try {
            Tutor tutor = tutorService.obter(id);
            List<Animal> animais = animalService.procurarPorTutor(id);
            
            model.addAttribute("tutor", tutor);
            model.addAttribute("animais", animais);
            model.addAttribute("pageTitle", "Tutor: " + tutor.getNome());
            model.addAttribute("breadcrumb", tutor.getNome());
            model.addAttribute("activePage", "tutores");
            
            return "tutores/detail";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Tutor não encontrado");
            return "redirect:/tutores";
        }
    }
    
    /**
     * GET /tutores/{id}/editar - Formulário para editar um tutor.
     */
    @GetMapping("/{id}/editar")
    public String editarForm(
        @PathVariable Long id,
        Model model
    ) {
        try {
            Tutor tutor = tutorService.obter(id);
            
            TutorFormDto formDto = new TutorFormDto();
            formDto.setNome(tutor.getNome());
            formDto.setNif(tutor.getNif());
            formDto.setContacto(tutor.getContacto());
            formDto.setEmail(tutor.getEmail());
            
            model.addAttribute("tutorForm", formDto);
            model.addAttribute("tutorId", id);
            model.addAttribute("pageTitle", "Editar Tutor");
            model.addAttribute("breadcrumb", "Editar: " + tutor.getNome());
            model.addAttribute("activePage", "tutores");
            
            return "tutores/form";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Tutor não encontrado");
            return "redirect:/tutores";
        }
    }
    
    /**
     * POST /tutores/{id}/editar - Atualiza um tutor.
     */
    @PostMapping("/{id}")
    public String atualizar(
        @PathVariable Long id,
        @Valid @ModelAttribute("tutorForm") TutorFormDto formDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tutorId", id);
            model.addAttribute("pageTitle", "Editar Tutor");
            model.addAttribute("activePage", "tutores");
            return "tutores/form";
        }
        
        try {
            Tutor tutor = tutorService.atualizar(id, formDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Tutor atualizado com sucesso!");
            return "redirect:/tutores/" + tutor.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("tutorId", id);
            model.addAttribute("pageTitle", "Editar Tutor");
            model.addAttribute("activePage", "tutores");
            return "tutores/form";
        }
    }
    
    // ==================== ANIMAL ENDPOINTS ====================
    
    /**
     * GET /tutores/{tutorId}/animais/novo - Formulário para registar um animal.
     */
    @GetMapping("/{tutorId}/animais/novo")
    public String novoAnimalForm(
        @PathVariable Long tutorId,
        Model model
    ) {
        try {
            Tutor tutor = tutorService.obter(tutorId);
            
            AnimalFormDto animalForm = new AnimalFormDto();
            animalForm.setTutorId(tutorId);
            
            model.addAttribute("animalForm", animalForm);
            model.addAttribute("tutor", tutor);
            model.addAttribute("pageTitle", "Novo Animal");
            model.addAttribute("breadcrumb", "Registar Animal para " + tutor.getNome());
            model.addAttribute("activePage", "animais");
            
            return "animais/form";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Tutor não encontrado");
            return "redirect:/tutores";
        }
    }
    
    /**
     * POST /tutores/{tutorId}/animais - Cria um novo animal.
     */
    @PostMapping("/{tutorId}/animais")
    public String criarAnimal(
        @PathVariable Long tutorId,
        @Valid @ModelAttribute("animalForm") AnimalFormDto formDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        try {
            Tutor tutor = tutorService.obter(tutorId);
            formDto.setTutorId(tutorId);
            
            if (bindingResult.hasErrors()) {
                model.addAttribute("tutor", tutor);
                model.addAttribute("pageTitle", "Novo Animal");
                model.addAttribute("activePage", "animais");
                return "animais/form";
            }
            
            Animal animal = animalService.registar(formDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Animal '" + animal.getNome() + "' registado com sucesso!");
            return "redirect:/tutores/" + tutorId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Novo Animal");
            model.addAttribute("activePage", "animais");
            return "animais/form";
        }
    }
}
