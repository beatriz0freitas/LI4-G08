package pt.hotel.animais.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.hotel.animais.dto.ColaboradorFormDto;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.enums.TipoColaborador;
import pt.hotel.animais.service.IColaboradorService;

@Controller
@RequestMapping("/colaboradores")
@PreAuthorize("hasRole('DIRETOR')")
public class ColaboradorController {

    private final IColaboradorService colaboradorService;

    public ColaboradorController(IColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("colaboradores", colaboradorService.listarTodos());
        model.addAttribute("pageTitle", "Colaboradores");
        model.addAttribute("activePage", "colaboradores");
        return "colaboradores/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        prepararFormulario(model, new ColaboradorFormDto(), null, "Novo Colaborador");
        return "colaboradores/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("colaboradorForm") ColaboradorFormDto formDto,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, formDto, null, "Novo Colaborador");
            return "colaboradores/form";
        }

        try {
            Colaborador colaborador = colaboradorService.criar(formDto);
            redirectAttributes.addFlashAttribute("successMessage", "Colaborador '" + colaborador.getNome() + "' criado com sucesso.");
            return "redirect:/colaboradores";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            prepararFormulario(model, formDto, null, "Novo Colaborador");
            return "colaboradores/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Colaborador colaborador = colaboradorService.obter(id);
        ColaboradorFormDto formDto = new ColaboradorFormDto();
        formDto.setUsername(colaborador.getUsername());
        formDto.setNome(colaborador.getNome());
        formDto.setEmail(colaborador.getEmail());
        formDto.setTipoColaborador(colaborador.getTipoColaborador());
        formDto.setAtivo(colaborador.isAtivo());
        prepararFormulario(model, formDto, id, "Editar Colaborador");
        return "colaboradores/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("colaboradorForm") ColaboradorFormDto formDto,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, formDto, id, "Editar Colaborador");
            return "colaboradores/form";
        }

        try {
            colaboradorService.atualizar(id, formDto);
            redirectAttributes.addFlashAttribute("successMessage", "Colaborador atualizado com sucesso.");
            return "redirect:/colaboradores";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            prepararFormulario(model, formDto, id, "Editar Colaborador");
            return "colaboradores/form";
        }
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        colaboradorService.desativar(id);
        redirectAttributes.addFlashAttribute("successMessage", "Colaborador desativado com sucesso.");
        return "redirect:/colaboradores";
    }

    private void prepararFormulario(Model model, ColaboradorFormDto formDto, Long colaboradorId, String title) {
        model.addAttribute("colaboradorForm", formDto);
        model.addAttribute("colaboradorId", colaboradorId);
        model.addAttribute("tiposColaborador", TipoColaborador.values());
        model.addAttribute("pageTitle", title);
        model.addAttribute("activePage", "colaboradores");
    }
}
