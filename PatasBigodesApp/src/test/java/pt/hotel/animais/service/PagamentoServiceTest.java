package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.model.*;
import pt.hotel.animais.model.enums.*;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.repository.PagamentoRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private EstadiaRepository estadiaRepository;

    @Mock
    private ServicoExtraRepository servicoExtraRepository;

    @Mock
    private IntervencaoClinicaRepository intervencaoClinicaRepository;

    @Mock
    private TipoAlojamentoTarifaService tipoAlojamentoTarifaService;

    @Mock
    private AuditoriaOperacaoService auditoriaOperacaoService;

    @InjectMocks
    private PagamentoService pagamentoService;

    @Test
    void calcularValorBaseComTarifaDinâmica() {
        Estadia estadia = criarEstadiaComReserva(1L, 2);
        when(tipoAlojamentoTarifaService.obterValorTarifa("CANINO"))
                .thenReturn(new BigDecimal("15.00"));

        BigDecimal valor = pagamentoService.calcularValorBase(estadia);

        assertThat(valor).isEqualByComparingTo(new BigDecimal("30.00")); // 2 dias × 15.00
        verify(tipoAlojamentoTarifaService).obterValorTarifa("CANINO");
    }

    @Test
    void calcularValorBaseLançaExcecaoSeTarifaNaoConfigurada() {
        Estadia estadia = criarEstadiaComReserva(1L, 1);
        when(tipoAlojamentoTarifaService.obterValorTarifa(any()))
                .thenThrow(new IllegalArgumentException("Tarifa não configurada"));

        assertThatThrownBy(() -> pagamentoService.calcularValorBase(estadia))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tarifa");
    }

    @Test
    void registrarPagamentoDevePersistirComEstadoLiquidado() {
        Estadia estadia = criarEstadia(1L);
        PagamentoDto dto = new PagamentoDto();
        dto.setEstadiaId(1L);
        dto.setValor(new BigDecimal("50.00"));
        dto.setMetodoPagamento(MetodoPagamento.CARTAO_DEBITO);
        dto.setMomentoPagamento(MomentoPagamento.CHECK_IN);
        dto.setEstadoPagamento(EstadoPagamento.LIQUIDADO);

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Pagamento resultado = pagamentoService.registrarPagamento(dto);

        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(resultado.getEstadoPagamento()).isEqualTo(EstadoPagamento.LIQUIDADO);
        assertThat(resultado.getMetodoPagamento()).isEqualTo(MetodoPagamento.CARTAO_DEBITO);
        assertThat(resultado.getMomentoPagamento()).isEqualTo(MomentoPagamento.CHECK_IN);
        verify(pagamentoRepository).save(any(Pagamento.class));
    }

    @Test
    void registrarPagamentoDeveLancarExcecaoSeMetodoNulo() {
        Estadia estadia = criarEstadia(1L);
        PagamentoDto dto = new PagamentoDto();
        dto.setEstadiaId(1L);
        dto.setValor(BigDecimal.TEN);
        dto.setMetodoPagamento(null); // Inválido: método obrigatório
        dto.setMomentoPagamento(MomentoPagamento.CHECK_IN);

        assertThatThrownBy(() -> pagamentoService.registrarPagamento(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatório");

        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void registrarPagamentoCheckOutCom2Parametros() {
        Estadia estadia = criarEstadiaComReserva(1L, 2);
        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(servicoExtraRepository.sumCustoByEstadiaId(1L)).thenReturn(BigDecimal.ZERO);
        when(intervencaoClinicaRepository.sumCustoByEstadiaId(1L)).thenReturn(BigDecimal.ZERO);

        Pagamento resultado = pagamentoService.registrarPagamentoCheckOut(1L, MetodoPagamento.NUMERARIO);

        assertThat(resultado.getMomentoPagamento()).isEqualTo(MomentoPagamento.CHECK_OUT);
        assertThat(resultado.getEstadoPagamento()).isEqualTo(EstadoPagamento.LIQUIDADO);
        assertThat(resultado.getMetodoPagamento()).isEqualTo(MetodoPagamento.NUMERARIO);
    }

    @Test
    void registrarPagamentoCheckOutDeveLancarExcecaoSeMetodoNulo() {
        assertThatThrownBy(() -> pagamentoService.registrarPagamentoCheckOut(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatório");

        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void calcularCobrancaComplementarComExtrasPositivos() {
        Estadia estadia = criarEstadiaComReserva(1L, 2);
        estadia.setDataFim(LocalDateTime.now().plusDays(1)); // 1 dia extra

        when(servicoExtraRepository.sumCustoByEstadiaId(1L)).thenReturn(new BigDecimal("20.00"));
        when(intervencaoClinicaRepository.sumCustoByEstadiaId(1L)).thenReturn(new BigDecimal("30.00"));
        when(tipoAlojamentoTarifaService.obterValorTarifa("CANINO"))
                .thenReturn(new BigDecimal("15.00"));

        BigDecimal valor = pagamentoService.calcularCobrancaComplementar(estadia);

        // 1 dia extra (15) + 20 (extra) + 30 (clínica) = 65
        assertThat(valor).isEqualByComparingTo(new BigDecimal("65.00"));
    }

    @Test
    void calcularCobrancaComplementarSemExtras() {
        Estadia estadia = criarEstadiaComReserva(1L, 2);
        estadia.setDataFim(LocalDateTime.now()); // Sem dias extra

        when(servicoExtraRepository.sumCustoByEstadiaId(1L)).thenReturn(null);
        when(intervencaoClinicaRepository.sumCustoByEstadiaId(1L)).thenReturn(null);

        BigDecimal valor = pagamentoService.calcularCobrancaComplementar(estadia);

        assertThat(valor).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calcularExtrasRetornaValorAgregado() {
        Estadia estadia = criarEstadiaComReserva(1L, 2);
        estadia.setDataFim(LocalDateTime.now());
        when(servicoExtraRepository.sumCustoByEstadiaId(1L)).thenReturn(new BigDecimal("25.00"));
        when(intervencaoClinicaRepository.sumCustoByEstadiaId(1L)).thenReturn(new BigDecimal("35.00"));

        BigDecimal extras = pagamentoService.calcularCobrancaComplementar(estadia);

        assertThat(extras).isEqualByComparingTo(new BigDecimal("60.00"));
    }

    private Estadia criarEstadia(Long id) {
        Estadia estadia = new Estadia();
        estadia.setId(id);
        estadia.setEstado(EstadoEstadia.EM_CURSO);
        estadia.setDataInicio(LocalDateTime.now());
        return estadia;
    }

    private Estadia criarEstadiaComReserva(Long id, int diasEstimados) {
        Estadia estadia = criarEstadia(id);
        
        LocalDate hoje = LocalDate.now();
        LocalDateTime dataInicioReserva = hoje.minusDays(diasEstimados).atStartOfDay();
        
        Reserva reserva = new Reserva();
        reserva.setId(100L);
        reserva.setDataInicio(dataInicioReserva.toLocalDate());
        // Reserva termina diasEstimados dias depois
        reserva.setDataFim(dataInicioReserva.toLocalDate().plusDays(diasEstimados));

        Alojamento alojamento = new Alojamento();
        alojamento.setTipo("CANINO");

        reserva.setAlojamento(alojamento);
        // Estadia começa no mesmo dia que a reserva
        estadia.setDataInicio(dataInicioReserva);
        estadia.setReserva(reserva);
        return estadia;
    }
}
