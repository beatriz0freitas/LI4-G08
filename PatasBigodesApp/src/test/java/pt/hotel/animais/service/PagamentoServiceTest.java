package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Pagamento;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.EstadoPagamento;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.model.enums.MomentoPagamento;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.PagamentoRepository;

import java.math.BigDecimal;
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

    @InjectMocks
    private PagamentoService pagamentoService;

    @Test
    void calcularValorBaseSemDataFimUsaUmDia() {
        Estadia estadia = new Estadia();
        estadia.setDataInicio(LocalDateTime.now());
        // dataFim = null → fallback para 1 dia

        BigDecimal valor = pagamentoService.calcularValorBase(estadia);

        assertThat(valor).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void calcularValorBaseComDoisDiasRetornaDuplicado() {
        Estadia estadia = new Estadia();
        LocalDateTime inicio = LocalDateTime.now().minusDays(2);
        estadia.setDataInicio(inicio);
        estadia.setDataFim(LocalDateTime.now());

        BigDecimal valor = pagamentoService.calcularValorBase(estadia);

        assertThat(valor).isEqualByComparingTo(new BigDecimal("20.00"));
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
        assertThat(resultado.getEstadia()).isNotNull();
        verify(pagamentoRepository).save(any(Pagamento.class));
    }

    @Test
    void registrarPagamentoUsaLiquidadoQuandoEstadoNulo() {
        Estadia estadia = criarEstadia(1L);
        PagamentoDto dto = new PagamentoDto();
        dto.setEstadiaId(1L);
        dto.setValor(BigDecimal.TEN);
        dto.setMetodoPagamento(MetodoPagamento.NAO_DEFINIDO);
        dto.setMomentoPagamento(MomentoPagamento.CHECK_IN);
        dto.setEstadoPagamento(null); // null → deve usar LIQUIDADO

        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Pagamento resultado = pagamentoService.registrarPagamento(dto);

        assertThat(resultado.getEstadoPagamento()).isEqualTo(EstadoPagamento.LIQUIDADO);
    }

    @Test
    void registrarPagamentoDeveLancarExcecaoSeEstadiaInexistente() {
        PagamentoDto dto = new PagamentoDto();
        dto.setEstadiaId(99L);
        when(estadiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagamentoService.registrarPagamento(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estadia");
    }

    @Test
    void registrarPagamentoCheckOutDevePersistirComMomentoCheckOut() {
        Estadia estadia = criarEstadia(1L);
        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Pagamento resultado = pagamentoService.registrarPagamentoCheckOut(1L, new BigDecimal("15.00"), MetodoPagamento.NUMERARIO);

        assertThat(resultado.getMomentoPagamento()).isEqualTo(MomentoPagamento.CHECK_OUT);
        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("15.00"));
        assertThat(resultado.getEstadoPagamento()).isEqualTo(EstadoPagamento.LIQUIDADO);
        assertThat(resultado.getEstadia()).isNotNull();
    }

    @Test
    void registrarPagamentoCheckOutUsaNaoDefinidoQuandoMetodoNulo() {
        Estadia estadia = criarEstadia(1L);
        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Pagamento resultado = pagamentoService.registrarPagamentoCheckOut(1L, BigDecimal.TEN, null);

        assertThat(resultado.getMetodoPagamento()).isEqualTo(MetodoPagamento.NAO_DEFINIDO);
    }

    @Test
    void calcularExtrasRetornaZero() {
        BigDecimal extras = pagamentoService.calcularExtras(criarEstadia(1L));

        assertThat(extras).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private Estadia criarEstadia(Long id) {
        Estadia estadia = new Estadia();
        estadia.setId(id);
        estadia.setEstado(EstadoEstadia.EM_CURSO);
        estadia.setDataInicio(LocalDateTime.now().minusDays(1));
        return estadia;
    }
}
