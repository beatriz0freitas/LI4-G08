package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LimpezaService implements ILimpezaService {
    private final AlojamentoRepository alojamentoRepository;
    private final AuditoriaOperacaoService auditoriaOperacaoService;

    public List<Alojamento> listarAlojamentosPendentes() {
        return alojamentoRepository.findByEstadoLimpeza(EstadoLimpeza.PENDENTE);
    }

    @Transactional
    public boolean marcarComoLimpo(Long id) {
        Optional<Alojamento> opt = alojamentoRepository.findById(id);
        if (opt.isPresent()) {
            Alojamento alojamento = opt.get();
            alojamento.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
            alojamentoRepository.save(alojamento);
            auditoriaOperacaoService.registarSucesso(
                "LIMPEZA_REALIZADA",
                "Alojamento",
                alojamento.getId(),
                "UPDATE",
                Map.of("estadoLimpeza", EstadoLimpeza.CONCLUIDO.name())
            );
            return true;
        }
        return false;
    }
}
