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

/**
 * Controller MVC responsável pela gestão de colaboradores pelo perfil de direção.
 *
 * Todas as ações devolvem páginas Thymeleaf ou redirecionamentos e nunca respostas
 * JSON. A autorização fica reforçada no controller porque criação, edição e
 * desativação de colaboradores alteram permissões de acesso ao sistema.
 */
@Controller
@RequestMapping("/colaboradores")
@PreAuthorize("hasRole('DIRETOR')")
public class ColaboradorController {

    private final IColaboradorService colaboradorService;

    public ColaboradorController(IColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

    /**
     * Lista todos os colaboradores registados.
     *
     * @param model modelo da página
     * @return template da lista de colaboradores
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("colaboradores", colaboradorService.listarTodos());
        model.addAttribute("pageTitle", "Colaboradores");
        model.addAttribute("activePage", "colaboradores");
        return "colaboradores/list";
    }

    /**
     * Apresenta o formulário de criação de colaborador.
     *
     * @param model modelo da página
     * @return template do formulário de colaborador
     */
    @GetMapping("/novo")
    public String novo(Model model) {
        prepararFormulario(model, new ColaboradorFormDto(), null, "Novo Colaborador");
        return "colaboradores/form";
    }

    /**
     * Processa a submissão do formulário de criação de colaborador.
     *
     * @param formDto dados submetidos pelo formulário
     * @param bindingResult resultado da validação Bean Validation
     * @param model modelo usado quando é necessário voltar ao formulário
     * @param redirectAttributes mensagens flash para o redirecionamento
     * @return redirecionamento para a lista ou template do formulário com erros
     */
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

    /**
     * Apresenta o formulário de edição preenchido com os dados atuais.
     *
     * @param id identificador do colaborador a editar
     * @param model modelo da página
     * @return template do formulário de colaborador
     */
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

    /**
     * Atualiza dados administrativos e permissões de um colaborador existente.
     *
     * @param id identificador do colaborador
     * @param formDto dados submetidos pelo formulário
     * @param bindingResult resultado da validação Bean Validation
     * @param model modelo usado quando é necessário voltar ao formulário
     * @param redirectAttributes mensagens flash para o redirecionamento
     * @return redirecionamento para a lista ou template do formulário com erros
     */
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

    /**
     * Desativa logicamente um colaborador sem apagar o histórico associado.
     *
     * @param id identificador do colaborador
     * @param redirectAttributes mensagens flash para o redirecionamento
     * @return redirecionamento para a lista de colaboradores
     */
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
