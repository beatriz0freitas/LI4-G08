package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.EstadoPagamento;
import pt.hotel.animais.model.enums.EstadoEstadia;
import pt.hotel.animais.model.enums.MetodoPagamento;
import pt.hotel.animais.model.enums.MomentoPagamento;
import pt.hotel.animais.repository.AnimalRepository;
import pt.hotel.animais.repository.EstadiaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Serviço para gestão do ciclo de vida da estadia.
 * Check-out é transacional (ACID): se qualquer passo falhar, tudo é revertido.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EstadiaService implements IEstadiaService {

    private final EstadiaRepository estadiaRepository;
    private final IReservaService reservaService;
    private final IPagamentoService pagamentoService;
    private final IAlojamentoService alojamentoService;
    private final AnimalRepository animalRepository;

    /**
     * Abre uma estadia a partir de uma reserva ativa.
     * Confirma a reserva, cria estadia e regista pagamento base na mesma transação.
     */
    public Estadia abrirEstadiaPorReserva(Long reservaId, MetodoPagamento metodoPagamento) {
        if (metodoPagamento == null) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório no check-in");
        }

        Reserva reserva = reservaService.obter(reservaId);

        if (!reserva.podeFazerCheckIn()) {
            throw new IllegalArgumentException("Reserva não está em estado válido para check-in");
        }

        Long animalId = reserva.getAnimal().getId();
        animalRepository.findByIdForUpdate(animalId)
            .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado"));
        estadiaRepository.findEmCursoPorAnimal(animalId).ifPresent(estadiaExistente -> {
            throw new EstadiaExistenteException(
                "O animal já tem uma estadia em curso. Termine a estadia atual antes de registar novo check-in."
            );
        });

        reserva = reservaService.confirmar(reservaId);

        Estadia estadia = new Estadia();
        estadia.setReserva(reserva);
        estadia.setDataInicio(LocalDateTime.now());
        estadia.setEstado(EstadoEstadia.EM_CURSO);

        Estadia saved = estadiaRepository.save(estadia);

        // Criar pagamento base no check-in (valor calculado automaticamente)
        BigDecimal valorBase = pagamentoService.calcularValorBase(saved);
        PagamentoDto pagamentoDto = new PagamentoDto();
        pagamentoDto.setEstadiaId(saved.getId());
        pagamentoDto.setValor(valorBase);
        pagamentoDto.setMetodoPagamento(metodoPagamento);
        pagamentoDto.setMomentoPagamento(MomentoPagamento.CHECK_IN);
        pagamentoDto.setEstadoPagamento(EstadoPagamento.LIQUIDADO);

        pagamentoService.registrarPagamento(pagamentoDto);

        return saved;
    }

    /**
     * Check-out transacional ACID: todas as operações numa única transação.
     * 
     * Sequência obrigatória (todos os passos devem suceder ou nada é feito):
     * 1. Validar que método de pagamento é real (obrigatório)
     * 2. Definir dataFim da estadia
     * 3. Calcular cobrança complementar
     * 4. Registar pagamento de check-out
     * 5. Marcar alojamento para limpeza
     * 6. Mudar estado para TERMINADA
     * 
     * Se qualquer passo falhar, a transação é revertida completamente.
     */
    @Transactional
    public Estadia checkOut(Long estadiaId, MetodoPagamento metodoPagamento) {
        if (metodoPagamento == null) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório no check-out");
        }

        Estadia estadia = estadiaRepository.findById(estadiaId)
                .orElseThrow(() -> new IllegalArgumentException("Estadia não encontrada"));

        if (estadia.getEstado() != EstadoEstadia.EM_CURSO) {
            throw new IllegalArgumentException("Estadia não está em curso");
        }

        // Passo 1 + 2: Definir dataFim (validação de método já feita acima)
        estadia.setDataFim(LocalDateTime.now());

        // Passo 3 + 4: Calcular e registar pagamento de check-out
        // O pagamento é registado mesmo que seja zero (para auditoria)
        pagamentoService.registrarPagamentoCheckOut(estadiaId, metodoPagamento);

        // Passo 5: Marcar alojamento para limpeza
        // Se isto falhar, a exceção propaga e reverte toda a transação
        var reserva = estadia.getReserva();
        if (reserva != null && reserva.getAlojamento() != null) {
            var alojamentoId = reserva.getAlojamento().getId();
            alojamentoService.marcarPendenteLimpeza(alojamentoId);
        }

        // Passo 6: Marcar como TERMINADA (último passo para garantir integridade)
        estadia.setEstado(EstadoEstadia.TERMINADA);
        Estadia saved = estadiaRepository.save(estadia);

        if (reserva != null) {
            reservaService.concluir(reserva.getId());
        }

        return saved;
    }

    /**
     * Conta as estadias em curso.
     */
    @Transactional(readOnly = true)
    public long contarEstadiasEmCurso() {
        return estadiaRepository.countByEstado(EstadoEstadia.EM_CURSO);
    }
}
