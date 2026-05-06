package pt.hotel.animais.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.service.AnimalService;

import java.util.List;

/**
 * Controller para gerenciar listagens gerais de animais.
 * Fornece endpoints raiz para visualizar todos os animais e detalhes.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/animais")
public class AnimalController {
    
    private final AnimalService animalService;
    
    /**
     * GET /animais - Lista geral de todos os animais.
     */
    @GetMapping
    public String listar(Model model) {
        List<Animal> animais = animalService.listarTodos();
        
        model.addAttribute("animais", animais);
        model.addAttribute("pageTitle", "Animais");
        model.addAttribute("breadcrumb", "Lista de Animais");
        model.addAttribute("activePage", "animais");
        
        return "animais/list";
    }
    
    /**
     * GET /animais/{id} - Detalhe de um animal.
     */
    @GetMapping("/{id}")
    public String detalhe(
        @PathVariable Long id,
        Model model
    ) {
        try {
            Animal animal = animalService.obter(id);
            
            model.addAttribute("animal", animal);
            model.addAttribute("pageTitle", "Animal: " + animal.getNome());
            model.addAttribute("breadcrumb", animal.getNome());
            model.addAttribute("activePage", "animais");
            
            return "animais/detail";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Animal não encontrado");
            return "redirect:/animais";
        }
    }
}
