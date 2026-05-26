package pt.hotel.animais.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.hotel.animais.dto.auditoria.AuditoriaEventoDTO;
import pt.hotel.animais.dto.auditoria.AuditoriaFiltroDTO;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.auditoria.AuditoriaEvento;
import pt.hotel.animais.model.enums.ResultadoAuditoria;
import pt.hotel.animais.service.IColaboradorService;
import pt.hotel.animais.service.auditoria.IAuditoriaService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller MVC para consulta e exportação da auditoria centralizada.
 *
 * O acesso é reservado ao perfil {@code DIRETOR}, de acordo com a política de
 * retenção e consulta definida para a LAC-13.
 */
@Controller
@RequestMapping("/auditoria")
@PreAuthorize("hasRole('DIRETOR')")
public class AuditoriaController {

    private static final int TAMANHO_PAGINA = 20;
    private static final int LIMITE_EXPORTACAO = 10_000;

    private final IAuditoriaService auditoriaService;
    private final IColaboradorService colaboradorService;
    private final ObjectMapper objectMapper;

    public AuditoriaController(IAuditoriaService auditoriaService,
                               IColaboradorService colaboradorService,
                               ObjectMapper objectMapper) {
        this.auditoriaService = auditoriaService;
        this.colaboradorService = colaboradorService;
        this.objectMapper = objectMapper;
    }

    /**
     * Lista eventos de auditoria com filtros por período, utilizador, operação e entidade.
     *
     * @param filtros filtros recebidos por query string
     * @param page número da página, começando em zero
     * @param model modelo MVC usado pelo template Thymeleaf
     * @return template da lista de auditoria
     */
    @GetMapping
    public String listar(@ModelAttribute("filtros") AuditoriaFiltroDTO filtros,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        normalizarPeriodo(filtros);
        int paginaAtual = Math.max(page, 0);
        Pageable pageable = PageRequest.of(paginaAtual, TAMANHO_PAGINA, Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<AuditoriaEventoDTO> eventos = consultarEventos(filtros, pageable);
        prepararModelo(model, filtros, eventos);
        return "auditoria/list";
    }

    /**
     * Exporta os eventos filtrados para CSV com cabeçalhos estáveis.
     *
     * @param filtros filtros recebidos por query string
     * @return resposta HTTP com ficheiro CSV
     */
    @GetMapping("/exportar/csv")
    public ResponseEntity<String> exportarCsv(@ModelAttribute("filtros") AuditoriaFiltroDTO filtros) {
        normalizarPeriodo(filtros);
        Pageable pageable = PageRequest.of(0, LIMITE_EXPORTACAO, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<AuditoriaEventoDTO> eventos = consultarEventos(filtros, pageable).getContent();

        String csv = gerarCsv(eventos);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDisposition(ContentDisposition.attachment().filename("auditoria.csv").build());
        return ResponseEntity.ok().headers(headers).body(csv);
    }

    private Page<AuditoriaEventoDTO> consultarEventos(AuditoriaFiltroDTO filtros, Pageable pageable) {
        return auditoriaService
            .consultarPorPeriodo(filtros.getDataInicio(), filtros.getDataFim(), filtros, pageable)
            .map(this::toDto);
    }

    private void prepararModelo(Model model, AuditoriaFiltroDTO filtros, Page<AuditoriaEventoDTO> eventos) {
        model.addAttribute("eventos", eventos);
        model.addAttribute("colaboradores", colaboradoresOrdenados());
        model.addAttribute("resultadosAuditoria", ResultadoAuditoria.values());
        model.addAttribute("pageTitle", "Auditoria");
        model.addAttribute("activePage", "auditoria");
        model.addAttribute("filtros", filtros);
    }

    private List<Colaborador> colaboradoresOrdenados() {
        return colaboradorService.listarTodos().stream()
            .sorted(Comparator.comparing(
                (Colaborador colaborador) -> colaborador.getNome() != null ? colaborador.getNome() : "",
                String.CASE_INSENSITIVE_ORDER
            ))
            .collect(Collectors.toList());
    }

    private void normalizarPeriodo(AuditoriaFiltroDTO filtros) {
        if (filtros.getDataInicio() == null) {
            filtros.setDataInicio(LocalDate.now().minusMonths(1));
        }
        if (filtros.getDataFim() == null) {
            filtros.setDataFim(LocalDate.now());
        }
        if (filtros.getDataInicio().isAfter(filtros.getDataFim())) {
            LocalDate dataInicio = filtros.getDataFim();
            filtros.setDataFim(filtros.getDataInicio());
            filtros.setDataInicio(dataInicio);
        }
    }

    private AuditoriaEventoDTO toDto(AuditoriaEvento evento) {
        AuditoriaEventoDTO dto = new AuditoriaEventoDTO();
        dto.setId(evento.getId());
        dto.setTimestamp(evento.getTimestamp());
        dto.setUtilizador(nomeUtilizador(evento));
        dto.setOperacao(evento.getOperacao());
        dto.setEntidade(evento.getEntidade());
        dto.setEntityId(evento.getEntityId());
        dto.setAcao(evento.getAcao());
        dto.setDetalhes(detalhesComoJson(evento.getDetalhes()));
        dto.setResultado(evento.getResultado());
        dto.setMotivoFalha(evento.getMotivoFalha());
        return dto;
    }

    private String nomeUtilizador(AuditoriaEvento evento) {
        if (evento.getUtilizador() == null) {
            return "-";
        }
        String nome = evento.getUtilizador().getNome();
        return nome != null && !nome.isBlank() ? nome : evento.getUtilizador().getUsername();
    }

    private String detalhesComoJson(Map<String, Object> detalhes) {
        if (detalhes == null || detalhes.isEmpty()) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(detalhes);
        } catch (JsonProcessingException exception) {
            return detalhes.toString();
        }
    }

    private String gerarCsv(List<AuditoriaEventoDTO> eventos) {
        StringBuilder csv = new StringBuilder();
        csv.append("id,timestamp,utilizador,operacao,entidade,entityId,acao,resultado,motivoFalha,detalhes\n");
        for (AuditoriaEventoDTO evento : eventos) {
            csv.append(campoCsv(evento.getId()))
                .append(',')
                .append(campoCsv(evento.getTimestamp()))
                .append(',')
                .append(campoCsv(evento.getUtilizador()))
                .append(',')
                .append(campoCsv(evento.getOperacao()))
                .append(',')
                .append(campoCsv(evento.getEntidade()))
                .append(',')
                .append(campoCsv(evento.getEntityId()))
                .append(',')
                .append(campoCsv(evento.getAcao()))
                .append(',')
                .append(campoCsv(evento.getResultado()))
                .append(',')
                .append(campoCsv(evento.getMotivoFalha()))
                .append(',')
                .append(campoCsv(evento.getDetalhes()))
                .append('\n');
        }
        return csv.toString();
    }

    private String campoCsv(Object valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor.toString();
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }
}
