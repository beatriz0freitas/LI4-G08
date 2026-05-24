package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import pt.hotel.animais.dto.RelatorioFiltroFormDto;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.PagamentoRepository;
import pt.hotel.animais.repository.ReservaRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        when(alojamentoRepository.count()).thenReturn(10L);
        when(estadiaRepository.countAlojamentosOcupadosAgora()).thenReturn(4L);
        when(estadiaRepository.countSobrepostasPeriodo(any(), any())).thenReturn(6L);
        when(reservaRepository.countInPeriod(any(), any())).thenReturn(8L);
        when(pagamentoRepository.sumValorPorPeriodo(any(), any())).thenReturn(new BigDecimal("120.00"));
        when(pagamentoRepository.countPendentesPorPeriodo(any(), any())).thenReturn(2L);
        when(pagamentoRepository.sumValorPorMetodo(any(), any()))
            .thenReturn(List.<Object[]>of(new Object[] {MetodoPagamento.NUMERARIO, new BigDecimal("50.00")}));
        when(servicoExtraRepository.sumCustoPorPeriodo(any(), any())).thenReturn(new BigDecimal("25.00"));

        var resumo = service.gerarRelatorio(filtro());

        assertThat(resumo.getTaxaOcupacao()).isEqualTo(40.0);
        assertThat(resumo.getEstadiasCount()).isEqualTo(6);
        assertThat(resumo.getReservasCount()).isEqualTo(8);
        assertThat(resumo.getFaturacaoTotal()).isEqualByComparingTo("120.00");
        assertThat(resumo.getServicosExtraTotal()).isEqualByComparingTo("25.00");
        assertThat(resumo.getFaturacaoPorMetodo()).containsEntry("NUMERARIO", new BigDecimal("50.00"));
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

    private RelatorioFiltroFormDto filtro() {
        RelatorioFiltroFormDto filtro = new RelatorioFiltroFormDto();
        filtro.setDataInicio(LocalDate.of(2026, 5, 1));
        filtro.setDataFim(LocalDate.of(2026, 5, 31));
        filtro.setIncluirServicosExtra(true);
        filtro.setAgruparPor(RelatorioFiltroFormDto.GrupoRelatorio.MES);
        return filtro;
    }
}
