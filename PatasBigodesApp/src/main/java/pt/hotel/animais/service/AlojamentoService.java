package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlojamentoService {

    private final AlojamentoRepository alojamentoRepository;

    public List<Alojamento> listarTodos() {
        return alojamentoRepository.findAllByOrderByIdentificacaoAsc();
    }

    public long contarAlojamentosDisponiveisDemo() {
        return alojamentoRepository.countByEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
    }
}
