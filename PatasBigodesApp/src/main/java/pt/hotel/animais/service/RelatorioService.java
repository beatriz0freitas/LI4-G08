package pt.hotel.animais.service;

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.RelatorioFiltroFormDto;
import pt.hotel.animais.dto.RelatorioResumoDto;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.PagamentoRepository;
import pt.hotel.animais.repository.ReservaRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço de aplicação para geração de relatórios operacionais e financeiros.
 *
 * Agrega dados de alojamentos, estadias, reservas, pagamentos e serviços extra.
 * Cada geração publica evento de auditoria com os filtros gerais utilizados, sem
 * expor detalhe financeiro linha a linha.
 */
@Service
@Transactional(readOnly = true)
public class RelatorioService implements IRelatorioService {

    private final AlojamentoRepository alojamentoRepository;
    private final EstadiaRepository estadiaRepository;
    private final ReservaRepository reservaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ServicoExtraRepository servicoExtraRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RelatorioService(AlojamentoRepository alojamentoRepository,
                            EstadiaRepository estadiaRepository,
                            ReservaRepository reservaRepository,
                            PagamentoRepository pagamentoRepository,
                            ServicoExtraRepository servicoExtraRepository,
                            ApplicationEventPublisher eventPublisher) {
        this.alojamentoRepository = alojamentoRepository;
        this.estadiaRepository = estadiaRepository;
        this.reservaRepository = reservaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.servicoExtraRepository = servicoExtraRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Calcula o resumo de métricas para o período e filtros indicados.
     *
     * @param filtro filtros de período, alojamento e serviços extra
     * @return resumo agregado para apresentação na página
     * @throws IllegalArgumentException quando o período é inválido
     */
    @Override
    public RelatorioResumoDto gerarRelatorio(RelatorioFiltroFormDto filtro) {
        validarPeriodo(filtro);
        LocalDateTime inicio = filtro.getDataInicio().atStartOfDay();
        LocalDateTime fim = filtro.getDataFim().atTime(LocalTime.MAX);

        RelatorioResumoDto resumo = new RelatorioResumoDto();
        long alojamentosTotal = filtro.getTipoAlojamento() == null
            ? alojamentoRepository.count()
            : alojamentoRepository.countByTipo(filtro.getTipoAlojamento());
        long ocupados = estadiaRepository.countAlojamentosOcupadosAgora();

        resumo.setAlojamentosTotal(alojamentosTotal);
        resumo.setAlojamentosOcupados(Math.min(ocupados, alojamentosTotal));
        resumo.setTaxaOcupacao(alojamentosTotal == 0 ? 0 : (resumo.getAlojamentosOcupados() * 100.0) / alojamentosTotal);
        resumo.setEstadiasCount(estadiaRepository.countSobrepostasPeriodo(inicio, fim));
        resumo.setReservasCount(reservaRepository.countInPeriod(filtro.getDataInicio(), filtro.getDataFim()));
        resumo.setFaturacaoTotal(pagamentoRepository.sumValorPorPeriodo(inicio, fim));
        resumo.setPagamentosPendentes(pagamentoRepository.countPendentesPorPeriodo(inicio, fim));
        resumo.setServicosExtraTotal(filtro.isIncluirServicosExtra()
            ? servicoExtraRepository.sumCustoPorPeriodo(inicio, fim)
            : BigDecimal.ZERO);
        resumo.setFaturacaoPorMetodo(faturacaoPorMetodo(inicio, fim));
        publicarAuditoria(filtro);
        return resumo;
    }

    /**
     * Gera uma representação CSV do relatório filtrado.
     *
     * @param filtro filtros aplicados ao relatório
     * @return conteúdo CSV com cabeçalhos estáveis
     */
    @Override
    public String gerarCsv(RelatorioFiltroFormDto filtro) {
        RelatorioResumoDto resumo = gerarRelatorio(filtro);
        return "periodo_start,periodo_end,ocupacaoPerc,estadiasCount,reservasCount,faturacaoTotal,pagamentosPendentes,servicosExtraTotal\n"
            + filtro.getDataInicio() + ","
            + filtro.getDataFim() + ","
            + String.format(java.util.Locale.US, "%.2f", resumo.getTaxaOcupacao()) + ","
            + resumo.getEstadiasCount() + ","
            + resumo.getReservasCount() + ","
            + resumo.getFaturacaoTotal() + ","
            + resumo.getPagamentosPendentes() + ","
            + resumo.getServicosExtraTotal() + "\n";
    }

    /**
     * Gera uma representação PDF simples do relatório filtrado.
     *
     * @param filtro filtros aplicados ao relatório
     * @return bytes do documento PDF simplificado
     */
    @Override
    //NOTA: REVER ISTO
    public byte[] gerarPdf(RelatorioFiltroFormDto filtro) {
        RelatorioResumoDto resumo = gerarRelatorio(filtro);
        List<String> linhas = new ArrayList<>();
        linhas.add("Relatório operacional e financeiro");
        linhas.add("Período: " + filtro.getDataInicio() + " a " + filtro.getDataFim());
        linhas.add("Alojamentos totais considerados: " + resumo.getAlojamentosTotal());
        linhas.add("Alojamentos ocupados: " + resumo.getAlojamentosOcupados());
        linhas.add("Taxa de ocupação: " + String.format(java.util.Locale.US, "%.2f%%", resumo.getTaxaOcupacao()));
        linhas.add("Estadias no período: " + resumo.getEstadiasCount());
        linhas.add("Reservas no período: " + resumo.getReservasCount());
        linhas.add("Faturação total: " + resumo.getFaturacaoTotal());
        linhas.add("Pagamentos pendentes: " + resumo.getPagamentosPendentes());
        linhas.add("Serviços extra: " + resumo.getServicosExtraTotal());
        linhas.add("Faturação por método:");
        resumo.getFaturacaoPorMetodo().forEach((metodo, valor) -> linhas.add(" - " + metodo + ": " + valor));
        return construirPdf(linhas);
    }

    private byte[] construirPdf(List<String> linhas) {
        StringBuilder stream = new StringBuilder();
        stream.append("BT\r\n");
        stream.append("/F1 18 Tf\r\n");
        stream.append("50 790 Td\r\n");

        for (int i = 0; i < linhas.size(); i++) {
            if (i == 1) {
                stream.append("/F1 11 Tf\r\n");
            }
            if (i > 0) {
                stream.append("0 -22 Td\r\n");
            }
            stream.append("(").append(escaparPdf(normalizarTextoPdf(linhas.get(i)))).append(") Tj\r\n");
        }
        stream.append("ET\r\n");

        byte[] streamBytes = stream.toString().getBytes(StandardCharsets.US_ASCII);
        List<String> objetos = List.of(
            "<< /Type /Catalog /Pages 2 0 R >>",
            "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
            "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
            "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
            "<< /Length " + streamBytes.length + " >>\r\nstream\r\n" + stream + "endstream"
        );

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        escreverPdf(pdf, "%PDF-1.4\r\n%\u00e2\u00e3\u00cf\u00d3\r\n");
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objetos.size(); i++) {
            offsets.add(pdf.size());
            escreverPdf(pdf, (i + 1) + " 0 obj\r\n");
            escreverPdf(pdf, objetos.get(i));
            escreverPdf(pdf, "\r\nendobj\r\n");
        }

        int xrefOffset = pdf.size();
        escreverPdf(pdf, "xref\r\n");
        escreverPdf(pdf, "0 " + (objetos.size() + 1) + "\r\n");
        escreverPdf(pdf, "0000000000 65535 f \r\n");
        for (Integer offset : offsets) {
            escreverPdf(pdf, String.format("%010d 00000 n \r\n", offset));
        }
        escreverPdf(pdf, "trailer\r\n");
        escreverPdf(pdf, "<< /Size " + (objetos.size() + 1) + " /Root 1 0 R >>\r\n");
        escreverPdf(pdf, "startxref\r\n");
        escreverPdf(pdf, xrefOffset + "\r\n");
        escreverPdf(pdf, "%%EOF\r\n");

        return pdf.toByteArray();
    }

    private String escaparPdf(String texto) {
        return texto
            .replace("\\", "\\\\")
            .replace("(", "\\(")
            .replace(")", "\\)");
    }

    private String normalizarTextoPdf(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .replaceAll("[^\\x20-\\x7E]", "");
    }

    private void escreverPdf(ByteArrayOutputStream output, String texto) {
        output.writeBytes(texto.getBytes(StandardCharsets.ISO_8859_1));
    }

    private Map<String, BigDecimal> faturacaoPorMetodo(LocalDateTime inicio, LocalDateTime fim) {
        Map<String, BigDecimal> valores = new LinkedHashMap<>();
        for (MetodoPagamento metodo : MetodoPagamento.values()) {
            valores.put(metodo.name(), BigDecimal.ZERO);
        }
        for (Object[] row : pagamentoRepository.sumValorPorMetodo(inicio, fim)) {
            valores.put(String.valueOf(row[0]), (BigDecimal) row[1]);
        }
        return valores;
    }

    private void validarPeriodo(RelatorioFiltroFormDto filtro) {
        if (filtro.getDataInicio() == null || filtro.getDataFim() == null) {
            throw new IllegalArgumentException("Período obrigatório");
        }
        if (filtro.getDataInicio().isAfter(filtro.getDataFim())) {
            throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim");
        }
        if (filtro.getAgruparPor() == null) {
            filtro.setAgruparPor(RelatorioFiltroFormDto.GrupoRelatorio.MES);
        }
    }

    /**
     * Constrói filtros para o mês corrente.
     *
     * @return filtro com início no primeiro dia do mês e fim no dia atual
     */
    public RelatorioFiltroFormDto filtroMesAtual() {
        LocalDate hoje = LocalDate.now();
        RelatorioFiltroFormDto filtro = new RelatorioFiltroFormDto();
        filtro.setDataInicio(hoje.withDayOfMonth(1));
        filtro.setDataFim(hoje);
        return filtro;
    }

    private void publicarAuditoria(RelatorioFiltroFormDto filtro) {
        eventPublisher.publishEvent(new AuditApplicationEvent(
            utilizadorAtual(),
            "RELATORIO_GERADO",
            Map.of(
                "dataInicio", filtro.getDataInicio().toString(),
                "dataFim", filtro.getDataFim().toString(),
                "incluirServicosExtra", filtro.isIncluirServicosExtra()
            )
        ));
    }

    private String utilizadorAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "sistema";
    }
}
