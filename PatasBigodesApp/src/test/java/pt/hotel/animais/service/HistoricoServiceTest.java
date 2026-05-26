package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pt.hotel.animais.dto.HistoricoFiltroDto;
import pt.hotel.animais.dto.HistoricoItemDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.IntervencaoClinica;
import pt.hotel.animais.model.Nota;
import pt.hotel.animais.model.RegistoCuidado;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.ServicoExtra;
import pt.hotel.animais.model.TipoServicoExtra;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.repository.NotaRepository;
import pt.hotel.animais.repository.RegistoCuidadoRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;

import java.math.BigDecimal;
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

    @InjectMocks
    private HistoricoService service;

    @Test
    void consultarSemEstadiaIdRetornaListaVazia() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        // estadiaId = null → não carrega nada

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10),
                regRepo, seRepo, icRepo, notaRepo);

        assertThat(resultado.getTotalElements()).isEqualTo(0);
        verifyNoInteractions(regRepo, seRepo, icRepo, notaRepo);
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
        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));
        when(notaRepo.findByReservaId(10L)).thenReturn(List.of(criarNota(4L, reserva)));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10),
                regRepo, seRepo, icRepo, notaRepo);

        assertThat(resultado.getTotalElements()).isEqualTo(4);
        assertThat(resultado.getContent())
                .extracting(HistoricoItemDto::getTipo)
                .containsExactlyInAnyOrder("REGISTO_CUIDADO", "SERVICO_EXTRA", "INTERVENCAO_CLINICA", "NOTA");
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
        when(estadiaRepository.findById(1L)).thenReturn(Optional.empty());

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10),
                regRepo, seRepo, icRepo, notaRepo);

        HistoricoItemDto item = resultado.getContent().get(0);
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
        when(estadiaRepository.findById(1L)).thenReturn(Optional.empty());

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10),
                regRepo, seRepo, icRepo, notaRepo);

        HistoricoItemDto item = resultado.getContent().get(0);
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
        when(estadiaRepository.findById(1L)).thenReturn(Optional.empty());

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10),
                regRepo, seRepo, icRepo, notaRepo);

        HistoricoItemDto item = resultado.getContent().get(0);
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
        when(estadiaRepository.findById(1L)).thenReturn(Optional.of(estadia));

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10),
                regRepo, seRepo, icRepo, notaRepo);

        assertThat(resultado.getTotalElements()).isEqualTo(0);
        verifyNoInteractions(notaRepo);
    }

    @Test
    void consultarQuandoEstadiaInexistenteNaoCarregaNotas() {
        HistoricoFiltroDto filtro = new HistoricoFiltroDto();
        filtro.setEstadiaId(99L);

        when(regRepo.findByEstadiaIdOrderByDataHoraDesc(99L)).thenReturn(List.of());
        when(seRepo.findByEstadiaId(99L)).thenReturn(List.of());
        when(icRepo.findByEstadiaId(99L)).thenReturn(List.of());
        when(estadiaRepository.findById(99L)).thenReturn(Optional.empty());

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10),
                regRepo, seRepo, icRepo, notaRepo);

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
        when(estadiaRepository.findById(1L)).thenReturn(Optional.empty());

        Page<HistoricoItemDto> resultado = service.consultar(filtro, PageRequest.of(0, 10),
                regRepo, seRepo, icRepo, notaRepo);

        assertThat(resultado.getContent().get(0).getId()).isEqualTo(2L);
        assertThat(resultado.getContent().get(1).getId()).isEqualTo(1L);
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
}
