package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.EstadoPagamento;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.model.enums.MomentoPagamento;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.service.PagamentoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class EstadiaService {

    private final EstadiaRepository estadiaRepository;
    private final ReservaService reservaService;
    private final PagamentoService pagamentoService;
    private final AlojamentoService alojamentoService;

    public Estadia abrirEstadiaPorReserva(Long reservaId) {
        Reserva reserva = reservaService.obter(reservaId);

        if (!reserva.isAtiva()) {
            throw new IllegalArgumentException("Reserva não está em estado válido para check-in");
        }

        Estadia estadia = new Estadia();
        estadia.setReserva(reserva);
        estadia.setDataInicio(LocalDateTime.now());
        estadia.setEstado(pt.hotel.animais.model.enums.EstadoEstadia.EM_CURSO);

        // marcar reserva como concluída para evitar reuso
        reservaService.concluir(reservaId);

        Estadia saved = estadiaRepository.save(estadia);

        // Criar pagamento base no check-in (valor calculado automaticamente)
        BigDecimal valorBase = pagamentoService.calcularValorBase(saved);
        PagamentoDto pagamentoDto = new PagamentoDto();
        pagamentoDto.setEstadiaId(saved.getId());
        pagamentoDto.setValor(valorBase);
        pagamentoDto.setMetodoPagamento(MetodoPagamento.NAO_DEFINIDO);
        pagamentoDto.setMomentoPagamento(MomentoPagamento.CHECK_IN);
        pagamentoDto.setEstadoPagamento(EstadoPagamento.LIQUIDADO);

        pagamentoService.registrarPagamento(pagamentoDto);

        return saved;
    }

    public Estadia checkOut(Long estadiaId) {
        Estadia estadia = estadiaRepository.findById(estadiaId)
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        if (estadia.getEstado() != pt.hotel.animais.model.enums.EstadoEstadia.EM_CURSO) {
            throw new IllegalArgumentException("Estadia não está em curso");
        }

        estadia.setDataFim(LocalDateTime.now());
        estadia.setEstado(pt.hotel.animais.model.enums.EstadoEstadia.TERMINADA);

        Estadia saved = estadiaRepository.save(estadia);

        // Calcular extras e registar pagamento de check-out (se aplicável)
        try {
            java.math.BigDecimal extras = pagamentoService.calcularExtras(saved);
            if (extras != null && extras.compareTo(java.math.BigDecimal.ZERO) > 0) {
                pagamentoService.registrarPagamentoCheckOut(saved.getId(), extras, MetodoPagamento.NAO_DEFINIDO);
            }
        } catch (Exception ignored) {
            // Não falhar o check-out se o registo de pagamento falhar
        }

        // Atualiza estado do alojamento para pendente de limpeza
        try {
            var reserva = saved.getReserva();
            if (reserva != null && reserva.getAlojamento() != null) {
                var alojamentoId = reserva.getAlojamento().getId();
                alojamentoService.marcarPendenteLimpeza(alojamentoId);
            }
        } catch (Exception ignored) {
            // Não falhar o check-out se atualização de alojamento não puder ocorrer
        }

        return saved;
    }

    /**
     * Conta as estadias em curso.
     */
    @Transactional(readOnly = true)
    public long contarEstadiasEmCurso() {
        return estadiaRepository.countByEstado(pt.hotel.animais.model.enums.EstadoEstadia.EM_CURSO);
    }
}
