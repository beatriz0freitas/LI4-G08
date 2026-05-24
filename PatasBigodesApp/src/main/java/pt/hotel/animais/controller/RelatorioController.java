package pt.hotel.animais.controller;

import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.hotel.animais.dto.RelatorioFiltroFormDto;
import pt.hotel.animais.model.enums.TipoAlojamento;
import pt.hotel.animais.service.IRelatorioService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Controller MVC dos relatórios operacionais e financeiros da direção.
 *
 * Renderiza a página Thymeleaf de filtros/resultados e disponibiliza downloads
 * CSV/PDF quando o utilizador pede exportação. As respostas de ficheiro usam
 * {@link ResponseEntity} apenas para controlar headers e conteúdo binário, não para
 * expor endpoints JSON.
 */
@Controller
@RequestMapping("/relatorios")
@PreAuthorize("hasRole('DIRETOR')")
public class RelatorioController {

    private final IRelatorioService relatorioService;

    public RelatorioController(IRelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    /**
     * Mostra a página de relatórios com filtros por defeito e resumo inicial.
     *
     * @param filtro filtros recebidos por query string
     * @param model modelo da página
     * @return template de relatórios
     */
    @GetMapping
    public String index(@ModelAttribute("filtro") RelatorioFiltroFormDto filtro, Model model) {
        aplicarDefaults(filtro);
        model.addAttribute("relatorio", relatorioService.gerarRelatorio(filtro));
        prepararModel(model);
        return "relatorios/list";
    }

    /**
     * Processa o formulário de filtros e recalcula as métricas apresentadas.
     *
     * @param filtro filtros submetidos pelo formulário
     * @param bindingResult resultado da validação Bean Validation
     * @param model modelo da página
     * @return template de relatórios com resultados ou mensagens de erro
     */
    @PostMapping("/gerar")
    public String gerar(@Valid @ModelAttribute("filtro") RelatorioFiltroFormDto filtro,
                        BindingResult bindingResult,
                        Model model) {
        if (bindingResult.hasErrors()) {
            prepararModel(model);
            return "relatorios/list";
        }
        try {
            model.addAttribute("relatorio", relatorioService.gerarRelatorio(filtro));
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        prepararModel(model);
        return "relatorios/list";
    }

    /**
     * Exporta o relatório filtrado para CSV.
     *
     * @param filtro filtros recebidos por query string
     * @return resposta de download com conteúdo CSV
     */
    @GetMapping("/exportar/csv")
    public ResponseEntity<byte[]> exportarCsv(@ModelAttribute RelatorioFiltroFormDto filtro) {
        aplicarDefaults(filtro);
        byte[] conteudo = relatorioService.gerarCsv(filtro).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("relatorio.csv").build().toString())
            .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
            .body(conteudo);
    }

    /**
     * Exporta o relatório filtrado para PDF.
     *
     * @param filtro filtros recebidos por query string
     * @return resposta de download com conteúdo PDF
     */
    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdf(@ModelAttribute RelatorioFiltroFormDto filtro) {
        aplicarDefaults(filtro);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("relatorio.pdf").build().toString())
            .contentType(MediaType.APPLICATION_PDF)
            .body(relatorioService.gerarPdf(filtro));
    }

    private void prepararModel(Model model) {
        model.addAttribute("tiposAlojamento", TipoAlojamento.values());
        model.addAttribute("gruposRelatorio", RelatorioFiltroFormDto.GrupoRelatorio.values());
        model.addAttribute("pageTitle", "Relatórios");
        model.addAttribute("activePage", "relatorios");
    }

    private void aplicarDefaults(RelatorioFiltroFormDto filtro) {
        LocalDate hoje = LocalDate.now();
        if (filtro.getDataInicio() == null) {
            filtro.setDataInicio(hoje.withDayOfMonth(1));
        }
        if (filtro.getDataFim() == null) {
            filtro.setDataFim(hoje);
        }
        if (filtro.getAgruparPor() == null) {
            filtro.setAgruparPor(RelatorioFiltroFormDto.GrupoRelatorio.MES);
        }
    }
}
