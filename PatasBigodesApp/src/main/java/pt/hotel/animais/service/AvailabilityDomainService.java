package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AvailabilityDomainService implements IAvailabilityDomainService {

    private final AlojamentoRepository alojamentoRepository;

    @Transactional(readOnly = true)
    public boolean estaDisponivel(Long alojamentoId, LocalDate dataInicio, LocalDate dataFim) {
        Alojamento alojamento = alojamentoRepository.findById(alojamentoId)
            .orElseThrow(() -> new IllegalArgumentException("Alojamento não encontrado"));
        return cumpreRegraDisponibilidade(alojamento, dataInicio, dataFim, null);
    }

    @Transactional(readOnly = true)
    public boolean estaDisponivel(Long alojamentoId, LocalDate dataInicio, LocalDate dataFim, Especie especie) {
        Alojamento alojamento = alojamentoRepository.findById(alojamentoId)
            .orElseThrow(() -> new IllegalArgumentException("Alojamento não encontrado"));
        return cumpreRegraDisponibilidade(alojamento, dataInicio, dataFim, especie);
    }

    public Alojamento validarDisponivelParaReservaComLock(
        Long alojamentoId,
        LocalDate dataInicio,
        LocalDate dataFim,
        Especie especie
    ) {
        Alojamento alojamento = alojamentoRepository.findByIdForUpdate(alojamentoId)
            .orElseThrow(() -> new IllegalArgumentException("Alojamento não encontrado"));

        if (!cumpreRegraDisponibilidade(alojamento, dataInicio, dataFim, especie)) {
            throw new IllegalArgumentException(
                "O alojamento não está disponível ou não é adequado à espécie do animal"
            );
        }

        return alojamento;
    }

    private boolean cumpreRegraDisponibilidade(
        Alojamento alojamento,
        LocalDate dataInicio,
        LocalDate dataFim,
        Especie especie
    ) {
        validarDatas(dataInicio, dataFim);

        if (alojamento.getEstadoLimpeza() != EstadoLimpeza.CONCLUIDO) {
            return false;
        }

        if (especie != null && !TipoAlojamentoPolicy.fromEspecie(especie).equals(alojamento.getTipo())) {
            return false;
        }

        long reservasSobrepostas = alojamentoRepository.countConflictingReservas(
            alojamento.getId(),
            dataInicio,
            dataFim
        );
        if (reservasSobrepostas > 0) {
            return false;
        }

        return alojamentoRepository.countEstadiasAtivasPorAlojamento(alojamento.getId()) == 0;
    }

    private void validarDatas(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Datas de entrada inválidas: dataInicio deve ser anterior a dataFim");
        }
    }
}
