package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pt.hotel.animais.dto.HistoricoFiltroDto;
import pt.hotel.animais.dto.HistoricoItemDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.IntervencaoClinica;
import pt.hotel.animais.model.Nota;
import pt.hotel.animais.model.Pagamento;
import pt.hotel.animais.model.RegistoCuidado;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.ServicoExtra;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.TipoServicoExtra;
import pt.hotel.animais.model.enums.EstadoPagamento;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.model.enums.MomentoPagamento;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.AlteracaoEstadoSaudeRepository;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.repository.NotaRepository;
import pt.hotel.animais.repository.PagamentoRepository;
import pt.hotel.animais.repository.RegistoCuidadoRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricoServiceTest {

    @Mock
    private EstadiaRepository estadiaRepository;

    // repositórios passados como parâmetro ao método consultar
    @Mock
    private RegistoCuidadoRepository regRepo;
    @Mock
    private ServicoExtraRepository seRepo;
    @Mock
    private IntervencaoClinicaRepository icRepo;
    @Mock
    private NotaRepository notaRepo;
    @Mock
    private AlteracaoEstadoSaudeRepository alteracaoRepo;
    @Mock
    private PagamentoRepository pagamentoRepo;

    @InjectMocks
    private HistoricoService service;

    @BeforeEach
    void configurarRepositoriosSemEventosPorOmissao() {
        lenient().when(regRepo.findByEstadiaIdOrderByDataHoraDesc(any())).thenReturn(List.of());
        lenient().when(seRepo.findByEstadiaId(any())).thenReturn(List.of());
        lenient().when(icRepo.findByEstadiaId(any())).thenReturn(List.of());
        lenient().when(notaRepo.findByReservaId(any())).thenReturn(List.of());
        lenient().when(alteracaoRepo.findByEstadiaIdOrderByDataHoraDesc(any())).thenReturn(List.of());
        lenient().when(pagamentoRepo.findByEstadiaId(any())).thenReturn(List.of());
    }

    @Test
    void consultarSemFiltrosRetornaListaVaziaQuandoNaoExistemEstadias() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        when(estadiaRepository.pesquisarHistorico(isNull(), isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(Page.empty());

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(0);
        verifyNoInteractions(regRepo, seRepo, icRepo, notaRepo, alteracaoRepo, pagamentoRepo);
    }

    @Test
    void consultarComEstadiaIdAgregaTodosOsTipos() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setEstadiaId(1L);

        Estadia estadia = criarEstadia(1L);
        Reserva reserva = new Reserva();
        reserva.setId(10L);
        estadia.setReserva(reserva);

        when(regRepo.findByEstadiaIdOrderByDataHoraDesc(1L)).thenReturn(List.of(criarRegistoCuidado(1L, estadia)));
        when(seRepo.findByEstadiaId(1L)).thenReturn(List.of(criarServicoExtra(2L, estadia)));
        when(icRepo.findByEstadiaId(1L)).thenReturn(List.of(criarIntervencao(3L, estadia)));
        when(estadiaRepository.findByIdComDetalhes(1L)).thenReturn(Optional.of(estadia));
        when(notaRepo.findByReservaId(10L)).thenReturn(List.of(criarNota(4L, reserva)));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(6);
        assertThat(resultado.getContent())
                .extracting(HistoricoItemDto::getTipo)
                .containsExactlyInAnyOrder("ESTADIA", "RESERVA", "REGISTO_CUIDADO", "SERVICO_EXTRA", "INTERVENCAO_CLINICA", "NOTA");
    }

    @Test
    void consultarDevePreencherCamposDoRegistoCuidado() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setEstadiaId(1L);

        Estadia estadia = criarEstadia(1L);
        RegistoCuidado rc = criarRegistoCuidado(7L, estadia);
        rc.setDescricao("Banho completo");

        when(regRepo.findByEstadiaIdOrderByDataHoraDesc(1L)).thenReturn(List.of(rc));
        when(seRepo.findByEstadiaId(1L)).thenReturn(List.of());
        when(icRepo.findByEstadiaId(1L)).thenReturn(List.of());
        when(estadiaRepository.findByIdComDetalhes(1L)).thenReturn(Optional.of(estadia));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        HistoricoItemDto item = resultado.getContent().stream()
                .filter(i -> "REGISTO_CUIDADO".equals(i.getTipo()))
                .findFirst()
                .orElseThrow();
        assertThat(item.getId()).isEqualTo(7L);
        assertThat(item.getEstadiaId()).isEqualTo(1L);
        assertThat(item.getDescricao()).isEqualTo("Banho completo");
        assertThat(item.getDataHora()).isNotNull();
        assertThat(item.getTipo()).isEqualTo("REGISTO_CUIDADO");
    }

    @Test
    void consultarDevePreencherCamposDoServicoExtra() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setEstadiaId(1L);

        Estadia estadia = criarEstadia(1L);
        ServicoExtra se = criarServicoExtra(8L, estadia);

        when(regRepo.findByEstadiaIdOrderByDataHoraDesc(1L)).thenReturn(List.of());
        when(seRepo.findByEstadiaId(1L)).thenReturn(List.of(se));
        when(icRepo.findByEstadiaId(1L)).thenReturn(List.of());
        when(estadiaRepository.findByIdComDetalhes(1L)).thenReturn(Optional.of(estadia));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        HistoricoItemDto item = resultado.getContent().stream()
                .filter(i -> "SERVICO_EXTRA".equals(i.getTipo()))
                .findFirst()
                .orElseThrow();
        assertThat(item.getId()).isEqualTo(8L);
        assertThat(item.getEstadiaId()).isEqualTo(1L);
        assertThat(item.getDataHora()).isNotNull();
        assertThat(item.getTipo()).isEqualTo("SERVICO_EXTRA");
    }

    @Test
    void consultarDevePreencherCamposDaIntervencao() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setEstadiaId(1L);

        Estadia estadia = criarEstadia(1L);
        IntervencaoClinica ic = criarIntervencao(9L, estadia);
        ic.setDescricao("Vacinação anual");

        when(regRepo.findByEstadiaIdOrderByDataHoraDesc(1L)).thenReturn(List.of());
        when(seRepo.findByEstadiaId(1L)).thenReturn(List.of());
        when(icRepo.findByEstadiaId(1L)).thenReturn(List.of(ic));
        when(estadiaRepository.findByIdComDetalhes(1L)).thenReturn(Optional.of(estadia));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        HistoricoItemDto item = resultado.getContent().stream()
                .filter(i -> "INTERVENCAO_CLINICA".equals(i.getTipo()))
                .findFirst()
                .orElseThrow();
        assertThat(item.getId()).isEqualTo(9L);
        assertThat(item.getDescricao()).isEqualTo("Vacinação anual");
        assertThat(item.getEstadiaId()).isEqualTo(1L);
        assertThat(item.getTipo()).isEqualTo("INTERVENCAO_CLINICA");
    }

    @Test
    void consultarSemReservaAssociadaNaoCarregaNotas() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setEstadiaId(1L);

        Estadia estadia = criarEstadia(1L);
        // sem reserva associada

        when(regRepo.findByEstadiaIdOrderByDataHoraDesc(1L)).thenReturn(List.of());
        when(seRepo.findByEstadiaId(1L)).thenReturn(List.of());
        when(icRepo.findByEstadiaId(1L)).thenReturn(List.of());
        when(estadiaRepository.findByIdComDetalhes(1L)).thenReturn(Optional.of(estadia));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getContent())
                .extracting(HistoricoItemDto::getTipo)
                .doesNotContain("NOTA");
        verifyNoInteractions(notaRepo);
    }

    @Test
    void consultarQuandoEstadiaInexistenteNaoCarregaNotas() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setEstadiaId(99L);

        when(estadiaRepository.findByIdComDetalhes(99L)).thenReturn(Optional.empty());

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(0);
        verifyNoInteractions(notaRepo);
    }

    @Test
    void consultarRetornaResultadosOrdenadosPorDataHoraDescendente() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setEstadiaId(1L);

        Estadia estadia = criarEstadia(1L);

        RegistoCuidado rc1 = criarRegistoCuidado(1L, estadia);
        rc1.setDataHora(LocalDateTime.now().minusHours(2));
        RegistoCuidado rc2 = criarRegistoCuidado(2L, estadia);
        rc2.setDataHora(LocalDateTime.now().minusMinutes(10));

        when(regRepo.findByEstadiaIdOrderByDataHoraDesc(1L)).thenReturn(List.of(rc1, rc2));
        when(seRepo.findByEstadiaId(1L)).thenReturn(List.of());
        when(icRepo.findByEstadiaId(1L)).thenReturn(List.of());
        when(estadiaRepository.findByIdComDetalhes(1L)).thenReturn(Optional.of(estadia));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        List<HistoricoItemDto> cuidados = resultado.getContent().stream()
                .filter(i -> "REGISTO_CUIDADO".equals(i.getTipo()))
                .toList();
        assertThat(cuidados.get(0).getId()).isEqualTo(2L);
        assertThat(cuidados.get(1).getId()).isEqualTo(1L);
    }

    @Test
    void consultarComAnimalClienteDatasETipoAplicaFiltrosComAnd() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setClienteId(20L);
        filtro.setAnimalId(10L);
        filtro.setDataInicio(LocalDate.now().minusDays(1));
        filtro.setDataFim(LocalDate.now());
        filtro.setTipoEvento("REGISTO_CUIDADO");

        Estadia estadia = criarEstadiaComReserva(1L, 20L, 10L);
        RegistoCuidado dentroDoIntervalo = criarRegistoCuidado(1L, estadia);
        dentroDoIntervalo.setDataHora(LocalDate.now().atTime(10, 0));
        RegistoCuidado foraDoIntervalo = criarRegistoCuidado(2L, estadia);
        foraDoIntervalo.setDataHora(LocalDate.now().minusDays(3).atTime(10, 0));

        when(estadiaRepository.pesquisarHistorico(eq(20L), eq(10L), isNull(), isNull(), isNull(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(estadia)));
        when(regRepo.findByEstadiaIdOrderByDataHoraDesc(1L))
                .thenReturn(List.of(dentroDoIntervalo, foraDoIntervalo));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getContent())
                .extracting(HistoricoItemDto::getId)
                .containsExactly(1L);
        assertThat(resultado.getContent())
                .extracting(HistoricoItemDto::getTipo)
                .containsExactly("REGISTO_CUIDADO");
    }

    @Test
    void consultarComClienteETipoEventoDevolveApenasEventosDoTipoPedido() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setClienteId(20L);
        filtro.setTipoEvento("SERVICO_EXTRA");

        Estadia estadia = criarEstadiaComReserva(1L, 20L, 10L);
        when(estadiaRepository.pesquisarHistorico(eq(20L), isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(estadia)));
        when(seRepo.findByEstadiaId(1L)).thenReturn(List.of(criarServicoExtra(8L, estadia)));
        when(icRepo.findByEstadiaId(1L)).thenReturn(List.of(criarIntervencao(9L, estadia)));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().getFirst().getTipo()).isEqualTo("SERVICO_EXTRA");
    }

    @Test
    void consultarComEstadiaEAnimalIncompativeisNaoDevolveEventos() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setEstadiaId(1L);
        filtro.setAnimalId(99L);

        Estadia estadia = criarEstadiaComReserva(1L, 20L, 10L);
        when(estadiaRepository.findByIdComDetalhes(1L)).thenReturn(Optional.of(estadia));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isZero();
        verifyNoInteractions(regRepo, seRepo, icRepo, notaRepo, alteracaoRepo, pagamentoRepo);
    }

    @Test
    void consultarIncluiPagamentosQuandoAplicavel() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setEstadiaId(1L);
        filtro.setTipoEvento("PAGAMENTO");

        Estadia estadia = criarEstadiaComReserva(1L, 20L, 10L);
        when(estadiaRepository.findByIdComDetalhes(1L)).thenReturn(Optional.of(estadia));
        when(pagamentoRepo.findByEstadiaId(1L)).thenReturn(List.of(criarPagamento(5L, estadia)));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().getFirst().getTipo()).isEqualTo("PAGAMENTO");
        assertThat(resultado.getContent().getFirst().getDescricao()).contains("CHECK_OUT");
    }

    @Test
    void listarHistoricoDeveDelegarNoRepositorio() {
        when(estadiaRepository.pesquisarHistorico(any(), any(), any(), any(), any(), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        service.listarHistorico(null, null, EstadoEstadia.TERMINADA,
                java.time.LocalDate.now().minusDays(7), java.time.LocalDate.now(),
                PageRequest.of(0, 10));

        verify(estadiaRepository).pesquisarHistorico(any(), any(), any(), any(), any(), any());
    }

    @Test
    void listarHistoricoComDatasNulasDeveDelegarNoRepositorio() {
        when(estadiaRepository.pesquisarHistorico(any(), any(), any(), any(), any(), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        service.listarHistorico(null, null, null, null, null, PageRequest.of(0, 10));

        verify(estadiaRepository).pesquisarHistorico(any(), any(), any(), isNull(), isNull(), any());
    }

    private Estadia criarEstadia(Long id) {
        Estadia e = new Estadia();
        e.setId(id);
        e.setEstado(EstadoEstadia.EM_CURSO);
        e.setDataInicio(LocalDateTime.now().minusDays(1));
        return e;
    }

    private Estadia criarEstadiaComReserva(Long estadiaId, Long clienteId, Long animalId) {
        Tutor tutor = new Tutor();
        tutor.setId(clienteId);
        Animal animal = new Animal();
        animal.setId(animalId);
        Reserva reserva = new Reserva();
        reserva.setId(estadiaId * 10);
        reserva.setTutor(tutor);
        reserva.setAnimal(animal);
        reserva.setEstado(pt.hotel.animais.model.enums.EstadoReserva.CONFIRMADA);
        reserva.setDataCriacao(LocalDateTime.now().minusDays(2));
        Estadia estadia = criarEstadia(estadiaId);
        estadia.setReserva(reserva);
        return estadia;
    }

    private RegistoCuidado criarRegistoCuidado(Long id, Estadia estadia) {
        RegistoCuidado rc = new RegistoCuidado();
        rc.setId(id);
        rc.setEstadia(estadia);
        rc.setDescricao("Cuidado " + id);
        rc.setDataHora(LocalDateTime.now().minusHours(id));
        return rc;
    }

    private ServicoExtra criarServicoExtra(Long id, Estadia estadia) {
        ServicoExtra se = new ServicoExtra();
        se.setId(id);
        se.setEstadia(estadia);
        
        TipoServicoExtra tipo = new TipoServicoExtra("Serviço " + id, "Descrição do serviço");
        try {
            var tipoIdField = tipo.getClass().getDeclaredField("id");
            tipoIdField.setAccessible(true);
            tipoIdField.set(tipo, id * 100L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        se.setTipoServicoExtra(tipo);
        se.setCusto(new BigDecimal("10.00"));
        se.setDataHora(LocalDateTime.now().minusHours(id));
        return se;
    }

    private IntervencaoClinica criarIntervencao(Long id, Estadia estadia) {
        IntervencaoClinica ic = new IntervencaoClinica();
        ic.setId(id);
        ic.setEstadia(estadia);
        ic.setDescricao("Intervenção " + id);
        ic.setDataHora(LocalDateTime.now().minusHours(id));
        return ic;
    }

    private Nota criarNota(Long id, Reserva reserva) {
        Nota n = new Nota();
        n.setId(id);
        n.setReserva(reserva);
        n.setDescricao("Nota " + id);
        n.setDataHora(LocalDateTime.now().minusHours(id));
        return n;
    }

    private Pagamento criarPagamento(Long id, Estadia estadia) {
        Pagamento p = new Pagamento();
        p.setId(id);
        p.setEstadia(estadia);
        p.setValor(new BigDecimal("35.00"));
        p.setMetodoPagamento(MetodoPagamento.CARTAO_DEBITO);
        p.setMomentoPagamento(MomentoPagamento.CHECK_OUT);
        p.setEstadoPagamento(EstadoPagamento.LIQUIDADO);
        p.setDataCriacao(LocalDateTime.now());
        return p;
    }
}
