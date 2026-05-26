package pt.hotel.animais.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.HistoricoFiltroDto;
import pt.hotel.animais.dto.HistoricoItemDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Pagamento;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.repository.AlteracaoEstadoSaudeRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;
import pt.hotel.animais.repository.NotaRepository;
import pt.hotel.animais.repository.PagamentoRepository;
import pt.hotel.animais.repository.RegistoCuidadoRepository;
import pt.hotel.animais.repository.ServicoExtraRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HistoricoService implements IHistoricoService {

    private final EstadiaRepository estadiaRepository;
    private final RegistoCuidadoRepository registoCuidadoRepository;
    private final ServicoExtraRepository servicoExtraRepository;
    private final IntervencaoClinicaRepository intervencaoClinicaRepository;
    private final NotaRepository notaRepository;
    private final AlteracaoEstadoSaudeRepository alteracaoEstadoSaudeRepository;
    private final PagamentoRepository pagamentoRepository;

    public HistoricoService(
        EstadiaRepository estadiaRepository,
        RegistoCuidadoRepository registoCuidadoRepository,
        ServicoExtraRepository servicoExtraRepository,
        IntervencaoClinicaRepository intervencaoClinicaRepository,
        NotaRepository notaRepository,
        AlteracaoEstadoSaudeRepository alteracaoEstadoSaudeRepository,
        PagamentoRepository pagamentoRepository
    ) {
        this.estadiaRepository = estadiaRepository;
        this.registoCuidadoRepository = registoCuidadoRepository;
        this.servicoExtraRepository = servicoExtraRepository;
        this.intervencaoClinicaRepository = intervencaoClinicaRepository;
        this.notaRepository = notaRepository;
        this.alteracaoEstadoSaudeRepository = alteracaoEstadoSaudeRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    /**
     * Consulta consolidada do historial aplicando todos os filtros ativos com AND.
     */
    @Transactional(readOnly = true)
    public Page<HistoricoItemDto> consultar(HistoricoFiltroDto filtro, Pageable pageable) {
        HistoricoFiltroDto filtroSeguro = filtro != null ? filtro : new HistoricoFiltroDto();
        var items = new ArrayList<HistoricoItemDto>();

        for (Estadia estadia : obterEstadiasCandidatas(filtroSeguro)) {
            Long estadiaId = estadia.getId();
            Reserva reserva = estadia.getReserva();

            adicionar(items, "ESTADIA", estadiaId, estadiaId, descricaoEstadia(estadia), estadia.getDataInicio());

            if (reserva != null) {
                adicionar(items, "RESERVA", reserva.getId(), estadiaId, descricaoReserva(reserva),
                    reserva.getDataCriacao());

                notaRepository.findByReservaId(reserva.getId()).forEach(n ->
                    adicionar(items, "NOTA", n.getId(), estadiaId, n.getDescricao(), n.getDataHora()));
            }

            registoCuidadoRepository.findByEstadiaIdOrderByDataHoraDesc(estadiaId).forEach(c ->
                adicionar(items, "REGISTO_CUIDADO", c.getId(), estadiaId, c.getDescricao(), c.getDataHora()));

            servicoExtraRepository.findByEstadiaId(estadiaId).forEach(e -> {
                String nomeServico = e.getTipoServicoExtra() != null ? e.getTipoServicoExtra().getNome() : "Tipo não definido";
                adicionar(items, "SERVICO_EXTRA", e.getId(), estadiaId, nomeServico + " - €" + e.getCusto(), e.getDataHora());
            });

            intervencaoClinicaRepository.findByEstadiaId(estadiaId).forEach(i ->
                adicionar(items, "INTERVENCAO_CLINICA", i.getId(), estadiaId, i.getDescricao(), i.getDataHora()));

            alteracaoEstadoSaudeRepository.findByEstadiaIdOrderByDataHoraDesc(estadiaId).forEach(a ->
                adicionar(items, "ALTERACAO_ESTADO_SAUDE", a.getId(), estadiaId, a.getDescricao(), a.getDataHora()));

            pagamentoRepository.findByEstadiaId(estadiaId).forEach(p ->
                adicionar(items, "PAGAMENTO", p.getId(), estadiaId, descricaoPagamento(p), p.getDataCriacao()));
        }

        List<HistoricoItemDto> filtrados = items.stream()
            .filter(item -> correspondeTipo(item, filtroSeguro.getTipoEvento()))
            .filter(item -> correspondeIntervalo(item, filtroSeguro.getDataInicio(), filtroSeguro.getDataFim()))
            .sorted(Comparator.comparing(HistoricoItemDto::getDataHora, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
            .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtrados.size());
        List<HistoricoItemDto> pageContent = start > end ? List.of() : filtrados.subList(start, end);
        return new PageImpl<>(pageContent, pageable, filtrados.size());
    }

    private List<Estadia> obterEstadiasCandidatas(HistoricoFiltroDto filtro) {
        if (filtro.getEstadiaId() != null) {
            return estadiaRepository.findByIdComDetalhes(filtro.getEstadiaId())
                .filter(estadia -> correspondeCliente(estadia, filtro.getClienteId()))
                .filter(estadia -> correspondeAnimal(estadia, filtro.getAnimalId()))
                .map(List::of)
                .orElseGet(List::of);
        }

        return estadiaRepository.pesquisarHistorico(
            filtro.getClienteId(),
            filtro.getAnimalId(),
            null,
            null,
            null,
            Pageable.unpaged()
        ).getContent();
    }

    private boolean correspondeCliente(Estadia estadia, Long clienteId) {
        return clienteId == null
            || (estadia.getReserva() != null
                && estadia.getReserva().getTutor() != null
                && clienteId.equals(estadia.getReserva().getTutor().getId()));
    }

    private boolean correspondeAnimal(Estadia estadia, Long animalId) {
        return animalId == null
            || (estadia.getReserva() != null
                && estadia.getReserva().getAnimal() != null
                && animalId.equals(estadia.getReserva().getAnimal().getId()));
    }

    private void adicionar(List<HistoricoItemDto> items, String tipo, Long id, Long estadiaId, String descricao, LocalDateTime dataHora) {
        var h = new HistoricoItemDto();
        h.setTipo(tipo);
        h.setId(id);
        h.setEstadiaId(estadiaId);
        h.setDescricao(descricao);
        h.setDataHora(dataHora);
        items.add(h);
    }

    private boolean correspondeTipo(HistoricoItemDto item, String tipoEvento) {
        return tipoEvento == null || tipoEvento.isBlank() || tipoEvento.equals(item.getTipo());
    }

    private boolean correspondeIntervalo(HistoricoItemDto item, LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime dataHora = item.getDataHora();
        if (dataHora == null) {
            return dataInicio == null && dataFim == null;
        }
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.plusDays(1).atStartOfDay() : null;
        return (inicio == null || !dataHora.isBefore(inicio))
            && (fim == null || dataHora.isBefore(fim));
    }

    private String descricaoEstadia(Estadia estadia) {
        return "Estadia " + estadia.getEstado();
    }

    private String descricaoReserva(Reserva reserva) {
        return "Reserva " + reserva.getEstado();
    }

    private String descricaoPagamento(Pagamento pagamento) {
        return pagamento.getMomentoPagamento() + " - " + pagamento.getEstadoPagamento() + " - €" + pagamento.getValor();
    }

    @Transactional(readOnly = true)
    public Page<Estadia> listarHistorico(
        Long clienteId,
        Long animalId,
        EstadoEstadia estado,
        LocalDate dataInicio,
        LocalDate dataFim,
        Pageable pageable
    ) {
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.plusDays(1).atStartOfDay().minusNanos(1) : null;

        return estadiaRepository.pesquisarHistorico(clienteId, animalId, estado, inicio, fim, pageable);
    }
}
