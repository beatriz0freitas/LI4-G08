package pt.hotel.animais.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.IntervencaoClinicaFormDto;
import pt.hotel.animais.dto.RegistoCuidadoFormDto;
import pt.hotel.animais.dto.RelatorioFiltroFormDto;
import pt.hotel.animais.dto.RelatorioResumoDto;
import pt.hotel.animais.dto.ReservaFormDto;
import pt.hotel.animais.dto.ServicoExtraFormDto;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.EstadoPagamento;
import pt.hotel.animais.model.enums.EstadoReserva;
import pt.hotel.animais.model.enums.EstadoSaude;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.model.enums.MomentoPagamento;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.AnimalRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.repository.PagamentoRepository;
import pt.hotel.animais.repository.RegistoCuidadoRepository;
import pt.hotel.animais.repository.ReservaRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;
import pt.hotel.animais.repository.TutorRepository;
import pt.hotel.animais.service.EstadiaService;
import pt.hotel.animais.service.IntervencaoClinicaService;
import pt.hotel.animais.service.LimpezaService;
import pt.hotel.animais.service.RegistoCuidadoService;
import pt.hotel.animais.service.RelatorioService;
import pt.hotel.animais.service.ReservaService;
import pt.hotel.animais.service.ServicoExtraService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class FluxoOperacionalEndToEndIntegrationTest {

    private static final Long AUTOR_ID = 1L;

    @Autowired private ReservaService reservaService;
    @Autowired private EstadiaService estadiaService;
    @Autowired private RegistoCuidadoService registoCuidadoService;
    @Autowired private ServicoExtraService servicoExtraService;
    @Autowired private IntervencaoClinicaService intervencaoClinicaService;
    @Autowired private LimpezaService limpezaService;
    @Autowired private RelatorioService relatorioService;

    @Autowired private TutorRepository tutorRepository;
    @Autowired private AnimalRepository animalRepository;
    @Autowired private AlojamentoRepository alojamentoRepository;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private EstadiaRepository estadiaRepository;
    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private RegistoCuidadoRepository registoCuidadoRepository;
    @Autowired private ServicoExtraRepository servicoExtraRepository;
    @Autowired private IntervencaoClinicaRepository intervencaoClinicaRepository;

    @Test
    @WithMockUser(username = "vet.e2e", roles = {"DIRETOR", "MEDICO_VETERINARIO"})
    void fluxoCompletoReservaAteRelatorioDevePersistirEfeitosDoDominio() {
        Reserva reserva = criarReservaCompleta("E2E-001");

        Estadia estadia = estadiaService.abrirEstadiaPorReserva(reserva.getId(), MetodoPagamento.CARTAO_DEBITO);
        registoCuidadoService.create(cuidado(estadia.getId(), "Administração de medicação diária"), AUTOR_ID);
        servicoExtraService.register(servicoExtra(estadia.getId(), "Banho", "15.00"), AUTOR_ID);
        intervencaoClinicaService.register(intervencaoClinica(estadia.getId(), "Observação veterinária", "25.00"), AUTOR_ID);

        Estadia terminada = estadiaService.checkOut(estadia.getId(), MetodoPagamento.CARTAO_CREDITO);

        assertThat(terminada.getEstado()).isEqualTo(EstadoEstadia.TERMINADA);
        assertThat(terminada.getDataFim()).isNotNull();
        assertThat(reservaRepository.findById(reserva.getId()).orElseThrow().getEstado()).isEqualTo(EstadoReserva.CONCLUIDA);

        assertThat(registoCuidadoRepository.findByEstadiaIdOrderByDataHoraDesc(estadia.getId())).hasSize(1);
        assertThat(servicoExtraRepository.findByEstadiaId(estadia.getId())).hasSize(1);
        assertThat(intervencaoClinicaRepository.findByEstadiaId(estadia.getId())).hasSize(1);

        var pagamentos = pagamentoRepository.findByEstadiaId(estadia.getId());
        assertThat(pagamentos).hasSize(2);
        assertThat(pagamentos).allMatch(pagamento -> pagamento.getEstadoPagamento() == EstadoPagamento.LIQUIDADO);
        assertThat(pagamentos).anyMatch(pagamento -> pagamento.getMomentoPagamento() == MomentoPagamento.CHECK_IN);
        assertThat(pagamentos).anyMatch(pagamento ->
                pagamento.getMomentoPagamento() == MomentoPagamento.CHECK_OUT
                        && pagamento.getValor().compareTo(new BigDecimal("40.00")) == 0);

        Alojamento alojamento = alojamentoRepository.findById(reserva.getAlojamento().getId()).orElseThrow();
        assertThat(alojamento.getEstadoLimpeza()).isEqualTo(EstadoLimpeza.PENDENTE);
        assertThat(limpezaService.listarAlojamentosPendentes())
                .extracting(Alojamento::getId)
                .contains(alojamento.getId());

        assertThat(limpezaService.marcarComoLimpo(alojamento.getId())).isTrue();
        assertThat(alojamentoRepository.findById(alojamento.getId()).orElseThrow().getEstadoLimpeza())
                .isEqualTo(EstadoLimpeza.CONCLUIDO);

        RelatorioResumoDto relatorio = relatorioService.gerarRelatorio(relatorioHoje());
        assertThat(relatorio.getReservasCount()).isGreaterThanOrEqualTo(1);
        assertThat(relatorio.getEstadiasCount()).isGreaterThanOrEqualTo(1);
        assertThat(relatorio.getServicosExtraTotal()).isGreaterThanOrEqualTo(new BigDecimal("15.00"));
        assertThat(relatorio.getFaturacaoTotal()).isGreaterThanOrEqualTo(new BigDecimal("40.00"));
    }

    @Test
    @WithMockUser(username = "vet.e2e", roles = {"DIRETOR", "MEDICO_VETERINARIO"})
    void fluxoCompletoDeveBloquearOperacoesClinicasECuidadosDepoisDoCheckOut() {
        Reserva reserva = criarReservaCompleta("E2E-002");
        Estadia estadia = estadiaService.abrirEstadiaPorReserva(reserva.getId(), MetodoPagamento.NUMERARIO);

        estadiaService.checkOut(estadia.getId(), MetodoPagamento.NUMERARIO);

        assertThatThrownBy(() -> registoCuidadoService.create(cuidado(estadia.getId(), "Cuidado tardio"), AUTOR_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("em curso");
        assertThatThrownBy(() -> servicoExtraService.register(servicoExtra(estadia.getId(), "Passeio", "10.00"), AUTOR_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("em curso");
        assertThatThrownBy(() -> intervencaoClinicaService.register(intervencaoClinica(estadia.getId(), "Intervenção tardia", "20.00"), AUTOR_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("em curso");
    }

    private Reserva criarReservaCompleta(String sufixo) {
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Fluxo " + sufixo);
        tutor.setNif("NIF-" + sufixo);
        tutor.setContacto("930000000");
        tutor.setEmail("fluxo-" + sufixo + "@teste.pt");
        tutorRepository.saveAndFlush(tutor);

        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome("Animal Fluxo " + sufixo);
        animal.setEspecie(Especie.CAO);
        animal.setRaca("Serra da Estrela");
        animal.setDataNascimento(LocalDate.of(2021, 4, 12));
        animal.setPeso(new BigDecimal("18.50"));
        animal.setEstadoSaude(EstadoSaude.NORMAL);
        animalRepository.saveAndFlush(animal);

        Alojamento alojamento = alojamentoRepository.findAllByOrderByIdentificacaoAsc().stream()
                .filter(a -> a.getEstadoLimpeza() == EstadoLimpeza.CONCLUIDO)
                .findFirst()
                .orElseThrow();

        ReservaFormDto form = new ReservaFormDto();
        form.setTutorId(tutor.getId());
        form.setAnimalId(animal.getId());
        form.setAlojamentoId(alojamento.getId());
        form.setDataInicio(LocalDate.now());
        form.setDataFim(LocalDate.now().plusDays(3));

        return reservaService.criar(form);
    }

    private RegistoCuidadoFormDto cuidado(Long estadiaId, String descricao) {
        RegistoCuidadoFormDto form = new RegistoCuidadoFormDto();
        form.setEstadiaId(estadiaId);
        form.setDescricao(descricao);
        form.setDataHora(LocalDateTime.now());
        return form;
    }

    private ServicoExtraFormDto servicoExtra(Long estadiaId, String tipo, String custo) {
        ServicoExtraFormDto form = new ServicoExtraFormDto();
        form.setEstadiaId(estadiaId);
        form.setTipo(tipo);
        form.setCusto(new BigDecimal(custo));
        form.setDataHora(LocalDateTime.now());
        return form;
    }

    private IntervencaoClinicaFormDto intervencaoClinica(Long estadiaId, String descricao, String custo) {
        IntervencaoClinicaFormDto form = new IntervencaoClinicaFormDto();
        form.setEstadiaId(estadiaId);
        form.setDescricao(descricao);
        form.setCusto(new BigDecimal(custo));
        form.setDataHora(LocalDateTime.now());
        return form;
    }

    private RelatorioFiltroFormDto relatorioHoje() {
        RelatorioFiltroFormDto filtro = new RelatorioFiltroFormDto();
        filtro.setDataInicio(LocalDate.now());
        filtro.setDataFim(LocalDate.now());
        filtro.setIncluirServicosExtra(true);
        filtro.setAgruparPor(RelatorioFiltroFormDto.GrupoRelatorio.DIA);
        return filtro;
    }
}
