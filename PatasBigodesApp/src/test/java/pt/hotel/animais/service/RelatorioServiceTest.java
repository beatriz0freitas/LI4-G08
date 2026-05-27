package pt.hotel.animais.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import pt.hotel.animais.dto.RelatorioFiltroFormDto;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Pagamento;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.ServicoExtra;
import pt.hotel.animais.model.TipoServicoExtra;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.PagamentoRepository;
import pt.hotel.animais.repository.ReservaRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RelatorioServiceTest {

    private final AlojamentoRepository alojamentoRepository = mock(AlojamentoRepository.class);
    private final EstadiaRepository estadiaRepository = mock(EstadiaRepository.class);
    private final ReservaRepository reservaRepository = mock(ReservaRepository.class);
    private final PagamentoRepository pagamentoRepository = mock(PagamentoRepository.class);
    private final ServicoExtraRepository servicoExtraRepository = mock(ServicoExtraRepository.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

    private final RelatorioService service = new RelatorioService(
        alojamentoRepository,
        estadiaRepository,
        reservaRepository,
        pagamentoRepository,
        servicoExtraRepository,
        eventPublisher
    );

    @Test
    void gerarRelatorioDeveAgregarMetricasPrincipais() {
        configurarMocksBase();

        var resumo = service.gerarRelatorio(filtro());

        assertThat(resumo.getTaxaOcupacao()).isEqualTo(40.0);
        assertThat(resumo.getEstadiasCount()).isEqualTo(6);
        assertThat(resumo.getReservasCount()).isEqualTo(8);
        assertThat(resumo.getFaturacaoTotal()).isEqualByComparingTo("120.00");
        assertThat(resumo.getServicosExtraTotal()).isEqualByComparingTo("25.00");
        assertThat(resumo.getFaturacaoPorMetodo()).containsEntry("NUMERARIO", new BigDecimal("50.00"));
        assertThat(resumo.getAlojamentosTotal()).isEqualTo(10L);
        assertThat(resumo.getPagamentosPendentes()).isEqualTo(2L);
    }

    @Test
    void periodoInvalidoDeveFalhar() {
        RelatorioFiltroFormDto filtro = filtro();
        filtro.setDataInicio(LocalDate.of(2026, 5, 31));
        filtro.setDataFim(LocalDate.of(2026, 5, 1));

        assertThatThrownBy(() -> service.gerarRelatorio(filtro))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("data de início");
    }

    @Test
    void gerarCsvDeveConterCabecalhoEDadosDoRelatorio() {
        configurarMocksBase();

        String csv = service.gerarCsv(filtro());

        assertThat(csv).startsWith("periodo_start,periodo_end,");
        assertThat(csv).contains("agrupamento,reservas,estadias,faturacaoTotal,servicosExtraTotal");
        assertThat(csv).contains("2026-05-01");
        assertThat(csv).contains("2026-05-31");
        assertThat(csv).contains("40.00");
    }

    @Test
    void gerarPdfDeveSerParseavelEConterTextoEsperado() throws Exception {
        configurarMocksBase();

        byte[] pdf = service.gerarPdf(filtro());

        assertThat(new String(pdf, StandardCharsets.ISO_8859_1)).startsWith("%PDF-");
        try (var document = Loader.loadPDF(pdf)) {
            String texto = new PDFTextStripper().getText(document);
            assertThat(texto).contains("Relatório operacional e financeiro");
            assertThat(texto).contains("2026-05-01");
            assertThat(texto).contains("Agrupamentos:");
        }
    }

    @Test
    void gerarAgrupamentosDeveConsolidarDadosPorDia() {
        configurarMocksBase();
        when(reservaRepository.findByDataCriacaoBetweenOrderByDataCriacaoAsc(any(), any()))
            .thenReturn(List.of(reserva("A1", LocalDateTime.of(2026, 5, 1, 8, 0))));
        when(estadiaRepository.findByDataCriacaoBetweenOrderByDataCriacaoAsc(any(), any()))
            .thenReturn(List.of(estadia(LocalDateTime.of(2026, 5, 1, 10, 0))));
        when(pagamentoRepository.findByDataCriacaoBetweenOrderByDataCriacaoAsc(any(), any()))
            .thenReturn(List.of(pagamento(LocalDateTime.of(2026, 5, 1, 12, 0), new BigDecimal("80.00"))));
        when(servicoExtraRepository.findByDataHoraBetweenOrderByDataHoraAsc(any(), any()))
            .thenReturn(List.of(servicoExtra(LocalDateTime.of(2026, 5, 1, 13, 0), new BigDecimal("15.00"))));

        RelatorioFiltroFormDto filtro = filtro();
        filtro.setAgruparPor(RelatorioFiltroFormDto.GrupoRelatorio.DIA);

        var agrupamentos = service.gerarAgrupamentos(filtro);

        assertThat(agrupamentos).hasSize(1);
        assertThat(agrupamentos.get(0).getChave()).isEqualTo("2026-05-01");
        assertThat(agrupamentos.get(0).getReservas()).isEqualTo(1);
        assertThat(agrupamentos.get(0).getEstadias()).isEqualTo(1);
        assertThat(agrupamentos.get(0).getFaturacaoTotal()).isEqualByComparingTo("80.00");
        assertThat(agrupamentos.get(0).getServicosExtraTotal()).isEqualByComparingTo("15.00");
    }

    @Test
    void periodoSuperiorATresMesesDeveFalhar() {
        RelatorioFiltroFormDto filtro = filtro();
        filtro.setDataFim(LocalDate.of(2026, 8, 2));

        assertThatThrownBy(() -> service.gerarRelatorio(filtro))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("3 meses");
    }

    @Test
    void filtroMesAtualDeveRetornarPrimeiroDiaDeMesAteDia() {
        RelatorioFiltroFormDto filtro = service.filtroMesAtual();

        assertThat(filtro.getDataInicio().getDayOfMonth()).isEqualTo(1);
        assertThat(filtro.getDataFim()).isEqualTo(LocalDate.now());
        assertThat(filtro.getDataInicio().getMonth()).isEqualTo(LocalDate.now().getMonth());
    }

    @Test
    void gerarRelatorioSemServicosExtraRetornaZero() {
        configurarMocksBase();
        RelatorioFiltroFormDto filtro = filtro();
        filtro.setIncluirServicosExtra(false);

        var resumo = service.gerarRelatorio(filtro);

        assertThat(resumo.getServicosExtraTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void periodoComDatasNulasDeveFalhar() {
        RelatorioFiltroFormDto filtro = new RelatorioFiltroFormDto();
        filtro.setDataInicio(null);
        filtro.setDataFim(LocalDate.now());

        assertThatThrownBy(() -> service.gerarRelatorio(filtro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Período");
    }

    private void configurarMocksBase() {
        when(alojamentoRepository.count()).thenReturn(10L);
        when(estadiaRepository.countAlojamentosOcupadosAgora()).thenReturn(4L);
        when(estadiaRepository.countSobrepostasPeriodo(any(), any())).thenReturn(6L);
        when(reservaRepository.countInPeriod(any(), any())).thenReturn(8L);
        when(pagamentoRepository.sumValorPorPeriodo(any(), any())).thenReturn(new BigDecimal("120.00"));
        when(pagamentoRepository.countPendentesPorPeriodo(any(), any())).thenReturn(2L);
        when(pagamentoRepository.sumValorPorMetodo(any(), any()))
            .thenReturn(List.<Object[]>of(new Object[] {MetodoPagamento.NUMERARIO, new BigDecimal("50.00")}));
        when(servicoExtraRepository.sumCustoPorPeriodo(any(), any())).thenReturn(new BigDecimal("25.00"));
        when(reservaRepository.findByDataCriacaoBetweenOrderByDataCriacaoAsc(any(), any())).thenReturn(List.of());
        when(estadiaRepository.findByDataCriacaoBetweenOrderByDataCriacaoAsc(any(), any())).thenReturn(List.of());
        when(pagamentoRepository.findByDataCriacaoBetweenOrderByDataCriacaoAsc(any(), any())).thenReturn(List.of());
        when(servicoExtraRepository.findByDataHoraBetweenOrderByDataHoraAsc(any(), any())).thenReturn(List.of());
    }

    private RelatorioFiltroFormDto filtro() {
        RelatorioFiltroFormDto filtro = new RelatorioFiltroFormDto();
        filtro.setDataInicio(LocalDate.of(2026, 5, 1));
        filtro.setDataFim(LocalDate.of(2026, 5, 31));
        filtro.setIncluirServicosExtra(true);
        filtro.setAgruparPor(RelatorioFiltroFormDto.GrupoRelatorio.MES);
        return filtro;
    }

    private Reserva reserva(String identificacaoAlojamento, LocalDateTime dataCriacao) {
        Reserva reserva = new Reserva();
        reserva.setDataCriacao(dataCriacao);
        Alojamento alojamento = new Alojamento();
        alojamento.setIdentificacao(identificacaoAlojamento);
        reserva.setAlojamento(alojamento);
        return reserva;
    }

    private Estadia estadia(LocalDateTime dataCriacao) {
        Estadia estadia = new Estadia();
        estadia.setDataCriacao(dataCriacao);
        estadia.setReserva(reserva("A1", dataCriacao.minusHours(1)));
        return estadia;
    }

    private Pagamento pagamento(LocalDateTime dataCriacao, BigDecimal valor) {
        Pagamento pagamento = new Pagamento();
        pagamento.setDataCriacao(dataCriacao);
        pagamento.setValor(valor);
        pagamento.setEstadia(estadia(dataCriacao.minusHours(1)));
        return pagamento;
    }

    private ServicoExtra servicoExtra(LocalDateTime dataHora, BigDecimal custo) {
        ServicoExtra servicoExtra = new ServicoExtra();
        servicoExtra.setDataHora(dataHora);
        servicoExtra.setCusto(custo);
        servicoExtra.setAutorId(10L);
        TipoServicoExtra tipoServicoExtra = new TipoServicoExtra();
        tipoServicoExtra.setNome("Banho");
        servicoExtra.setTipoServicoExtra(tipoServicoExtra);
        servicoExtra.setEstadia(estadia(dataHora.minusHours(1)));
        return servicoExtra;
    }
}
