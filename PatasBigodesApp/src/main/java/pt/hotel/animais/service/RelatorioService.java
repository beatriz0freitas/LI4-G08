package pt.hotel.animais.service;

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.RelatorioAgrupamentoDto;
import pt.hotel.animais.dto.RelatorioFiltroFormDto;
import pt.hotel.animais.dto.RelatorioResumoDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Pagamento;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.ServicoExtra;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.PagamentoRepository;
import pt.hotel.animais.repository.ReservaRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

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
        resumo.setAgrupamentos(gerarAgrupamentos(filtro));
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
        StringBuilder csv = new StringBuilder();
        csv.append("periodo_start,periodo_end,ocupacaoPerc,estadiasCount,reservasCount,faturacaoTotal,pagamentosPendentes,servicosExtraTotal\n");
        csv.append(filtro.getDataInicio()).append(',')
            .append(filtro.getDataFim()).append(',')
            .append(String.format(Locale.US, "%.2f", resumo.getTaxaOcupacao())).append(',')
            .append(resumo.getEstadiasCount()).append(',')
            .append(resumo.getReservasCount()).append(',')
            .append(resumo.getFaturacaoTotal()).append(',')
            .append(resumo.getPagamentosPendentes()).append(',')
            .append(resumo.getServicosExtraTotal()).append('\n');
        csv.append("agrupamento,reservas,estadias,faturacaoTotal,servicosExtraTotal\n");
        for (RelatorioAgrupamentoDto agrupamento : resumo.getAgrupamentos()) {
            csv.append(agrupamento.getChave()).append(',')
                .append(agrupamento.getReservas()).append(',')
                .append(agrupamento.getEstadias()).append(',')
                .append(agrupamento.getFaturacaoTotal()).append(',')
                .append(agrupamento.getServicosExtraTotal()).append('\n');
        }
        return csv.toString();
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
        linhas.add("Agrupamentos:");
        for (RelatorioAgrupamentoDto agrupamento : resumo.getAgrupamentos()) {
            linhas.add(" - " + agrupamento.getChave()
                + " | reservas=" + agrupamento.getReservas()
                + " | estadias=" + agrupamento.getEstadias()
                + " | faturação=" + agrupamento.getFaturacaoTotal()
                + " | serviços extra=" + agrupamento.getServicosExtraTotal());
        }
        return construirPdf(linhas);
    }

    @Override
    public List<RelatorioAgrupamentoDto> gerarAgrupamentos(RelatorioFiltroFormDto filtro) {
        validarPeriodo(filtro);
        LocalDateTime inicio = filtro.getDataInicio().atStartOfDay();
        LocalDateTime fim = filtro.getDataFim().atTime(LocalTime.MAX);

        Map<String, RelatorioAgrupamentoDto> agrupamentos = new LinkedHashMap<>();
        for (Reserva reserva : reservaRepository.findByDataCriacaoBetweenOrderByDataCriacaoAsc(inicio, fim)) {
            adicionarReserva(agrupamentos, filtro.getAgruparPor(), reserva);
        }
        for (Estadia estadia : estadiaRepository.findByDataCriacaoBetweenOrderByDataCriacaoAsc(inicio, fim)) {
            adicionarEstadia(agrupamentos, filtro.getAgruparPor(), estadia);
        }
        for (Pagamento pagamento : pagamentoRepository.findByDataCriacaoBetweenOrderByDataCriacaoAsc(inicio, fim)) {
            adicionarPagamento(agrupamentos, filtro.getAgruparPor(), pagamento);
        }
        if (filtro.isIncluirServicosExtra()) {
            for (ServicoExtra servicoExtra : servicoExtraRepository.findByDataHoraBetweenOrderByDataHoraAsc(inicio, fim)) {
                adicionarServicoExtra(agrupamentos, filtro.getAgruparPor(), servicoExtra);
            }
        }

        return agrupamentos.values().stream()
            .sorted(Comparator.comparing(RelatorioAgrupamentoDto::getChave))
            .toList();
    }

    private byte[] construirPdf(List<String> linhas) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float y = 750f;
                boolean primeiro = true;
                for (String linha : linhas) {
                    contentStream.beginText();
                    if (primeiro) {
                        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD), 14);
                        primeiro = false;
                    } else {
                        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), 11);
                    }
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText(linha);
                    contentStream.endText();
                    y -= 15f;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao gerar PDF com PDFBox", e);
        }
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
        if (filtro.getDataInicio().plusMonths(3).isBefore(filtro.getDataFim())) {
            throw new IllegalArgumentException("Período máximo para exportação imediata é 3 meses. Selecione um intervalo menor ou contacte o suporte para processamento offline.");
        }
        if (filtro.getAgruparPor() == null) {
            filtro.setAgruparPor(RelatorioFiltroFormDto.GrupoRelatorio.MES);
        }
    }

    private void adicionarReserva(Map<String, RelatorioAgrupamentoDto> agrupamentos,
                                  RelatorioFiltroFormDto.GrupoRelatorio grupo,
                                  Reserva reserva) {
        agrupamento(agrupamentos, grupo, reserva.getDataCriacao(),
            chaveSecundariaPadrao(grupo, reserva.getAlojamento() != null ? reserva.getAlojamento().getIdentificacao() : null, "Sem colaborador", "Sem tipo de serviço"))
            .incrementarReservas();
    }

    private void adicionarEstadia(Map<String, RelatorioAgrupamentoDto> agrupamentos,
                                  RelatorioFiltroFormDto.GrupoRelatorio grupo,
                                  Estadia estadia) {
        agrupamento(agrupamentos, grupo, estadia.getDataCriacao(),
            chaveSecundariaPadrao(grupo,
                estadia.getReserva() != null && estadia.getReserva().getAlojamento() != null
                    ? estadia.getReserva().getAlojamento().getIdentificacao()
                    : null,
                "Sem colaborador",
                "Sem tipo de serviço"))
            .incrementarEstadias();
    }

    private void adicionarPagamento(Map<String, RelatorioAgrupamentoDto> agrupamentos,
                                    RelatorioFiltroFormDto.GrupoRelatorio grupo,
                                    Pagamento pagamento) {
        agrupamento(agrupamentos, grupo, pagamento.getDataCriacao(),
            chaveSecundariaPadrao(grupo,
                pagamento.getEstadia() != null && pagamento.getEstadia().getReserva() != null
                    && pagamento.getEstadia().getReserva().getAlojamento() != null
                    ? pagamento.getEstadia().getReserva().getAlojamento().getIdentificacao()
                    : null,
                "Sem colaborador",
                "Sem tipo de serviço"))
            .adicionarFaturacao(pagamento.getValor());
    }

    private void adicionarServicoExtra(Map<String, RelatorioAgrupamentoDto> agrupamentos,
                                       RelatorioFiltroFormDto.GrupoRelatorio grupo,
                                       ServicoExtra servicoExtra) {
        String chave = switch (grupo) {
            case COLABORADOR -> servicoExtra.getAutorId() == null ? "Sem colaborador" : "Colaborador " + servicoExtra.getAutorId();
            case TIPO_SERVICO -> servicoExtra.getTipoServicoExtra() == null ? "Sem tipo de serviço" : servicoExtra.getTipoServicoExtra().getNome();
            case ALOJAMENTO -> servicoExtra.getEstadia() != null && servicoExtra.getEstadia().getReserva() != null
                && servicoExtra.getEstadia().getReserva().getAlojamento() != null
                ? servicoExtra.getEstadia().getReserva().getAlojamento().getIdentificacao()
                : "Sem alojamento";
            default -> null;
        };
        if (chave == null) {
            chave = formatarChaveTemporal(grupo, servicoExtra.getDataHora());
        }
        agrupamento(agrupamentos, grupo, servicoExtra.getDataHora(), chave)
            .adicionarServicosExtra(servicoExtra.getCusto());
    }

    private String chaveSecundariaPadrao(RelatorioFiltroFormDto.GrupoRelatorio grupo,
                                         String alojamento,
                                         String colaborador,
                                         String tipoServico) {
        return switch (grupo) {
            case ALOJAMENTO -> alojamento != null ? alojamento : "Sem alojamento";
            case COLABORADOR -> colaborador;
            case TIPO_SERVICO -> tipoServico;
            default -> alojamento != null ? alojamento : "Sem alojamento";
        };
    }

    private RelatorioAgrupamentoDto agrupamento(Map<String, RelatorioAgrupamentoDto> agrupamentos,
                                                RelatorioFiltroFormDto.GrupoRelatorio grupo,
                                                LocalDateTime momento,
                                                String chaveSecundaria) {
        String chave = chaveAgrupamento(grupo, momento, chaveSecundaria);
        return agrupamentos.computeIfAbsent(chave, RelatorioAgrupamentoDto::new);
    }

    private String chaveAgrupamento(RelatorioFiltroFormDto.GrupoRelatorio grupo,
                                    LocalDateTime momento,
                                    String chaveSecundaria) {
        if (grupo == null) {
            grupo = RelatorioFiltroFormDto.GrupoRelatorio.MES;
        }
        return switch (grupo) {
            case DIA, SEMANA, MES -> formatarChaveTemporal(grupo, momento);
            case ALOJAMENTO, COLABORADOR, TIPO_SERVICO -> chaveSecundaria;
        };
    }

    private String formatarChaveTemporal(RelatorioFiltroFormDto.GrupoRelatorio grupo, LocalDateTime momento) {
        return switch (grupo == null ? RelatorioFiltroFormDto.GrupoRelatorio.MES : grupo) {
            case DIA -> momento.toLocalDate().toString();
            case SEMANA -> momento.getYear() + "-W" + String.format(Locale.ROOT, "%02d", momento.get(WeekFields.ISO.weekOfWeekBasedYear()));
            case MES -> momento.getYear() + "-" + String.format(Locale.ROOT, "%02d", momento.getMonthValue());
            default -> momento.toLocalDate().toString();
        };
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
