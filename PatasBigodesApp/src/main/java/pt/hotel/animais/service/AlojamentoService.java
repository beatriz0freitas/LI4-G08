package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.hotel.animais.dto.DisponibilidadeAlojamentoDto;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlojamentoService implements IAlojamentoService {

    private final AlojamentoRepository alojamentoRepository;

    public List<Alojamento> listarTodos() {
        return alojamentoRepository.findAllByOrderByIdentificacaoAsc();
    }

    /**
     * Conta os alojamentos disponíveis para receção.
     */
    public long contarAlojamentosDisponiveis() {
        return alojamentoRepository.countByEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
    }

    /**
     * Conta os alojamentos pendentes de limpeza.
     */
    public long contarAlojamentosPendentesLimpeza() {
        return alojamentoRepository.countByEstadoLimpeza(EstadoLimpeza.PENDENTE);
    }

    public long contarAlojamentosComReservasAtivas() {
        return alojamentoRepository.countAlojamentosComReservasAtivas();
    }
    
    /**
     * Procura alojamentos disponíveis para um período específico.
     * Um alojamento é considerado disponível se:
     * 1. O estado de limpeza é CONCLUIDO
     * 2. Não tem reservas ativas que se sobreponham com o período
     */
    public List<DisponibilidadeAlojamentoDto> consultarDisponibilidade(LocalDate dataInicio, LocalDate dataFim) {
        // Validação básica
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Datas de entrada inválidas: dataInicio deve ser anterior a dataFim");
        }
        
        // Procura alojamentos disponíveis através do repository
        List<Alojamento> alojamentosDisponiveis = alojamentoRepository.findAvailableForPeriod(dataInicio, dataFim);
        
        // Converte para DTOs
        return alojamentosDisponiveis.stream()
            .map(a -> toDisponibilidadeDto(a, dataInicio, dataFim))
            .collect(Collectors.toList());
    }

    /**
     * Procura alojamentos disponíveis para um período e adequados à espécie do animal.
     */
    public List<DisponibilidadeAlojamentoDto> consultarDisponibilidade(LocalDate dataInicio, LocalDate dataFim, Especie especie) {
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Datas de entrada inválidas: dataInicio deve ser anterior a dataFim");
        }

        String tipo = TipoAlojamentoPolicy.fromEspecie(especie);
        List<Alojamento> alojamentosDisponiveis = alojamentoRepository.findAvailableForPeriodAndTipo(dataInicio, dataFim, tipo);

        return alojamentosDisponiveis.stream()
            .map(a -> toDisponibilidadeDto(a, dataInicio, dataFim))
            .collect(Collectors.toList());
    }
    
    /**
     * Verifica se um alojamento específico está disponível para um período.
     */
    public boolean estaDisponivel(Long alojamentoId, LocalDate dataInicio, LocalDate dataFim) {
        // Obtém o alojamento
        Alojamento alojamento = alojamentoRepository.findById(alojamentoId)
            .orElseThrow(() -> new IllegalArgumentException("Alojamento não encontrado"));
        
        // Verifica se está limpo
        if (alojamento.getEstadoLimpeza() != EstadoLimpeza.CONCLUIDO) {
            return false;
        }
        
        // Verifica conflitos de reserva
        long conflitos = alojamentoRepository.countConflictingReservas(alojamentoId, dataInicio, dataFim);
        return conflitos == 0;
    }

    /**
     * Verifica disponibilidade e compatibilidade com a espécie do animal.
     */
    public boolean estaDisponivel(Long alojamentoId, LocalDate dataInicio, LocalDate dataFim, Especie especie) {
        Alojamento alojamento = alojamentoRepository.findById(alojamentoId)
            .orElseThrow(() -> new IllegalArgumentException("Alojamento não encontrado"));

        String tipoEsperado = TipoAlojamentoPolicy.fromEspecie(especie);
        if (!tipoEsperado.equals(alojamento.getTipo())) {
            return false;
        }

        return estaDisponivel(alojamentoId, dataInicio, dataFim);
    }
    
    /**
     * Obtém um alojamento por ID.
     */
    public Alojamento obter(Long id) {
        return alojamentoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Alojamento com ID " + id + " não encontrado"));
    }

    /**
     * Marca um alojamento como pendente de limpeza após check-out.
     */
    public void marcarPendenteLimpeza(Long alojamentoId) {
        Alojamento alojamento = alojamentoRepository.findById(alojamentoId)
            .orElseThrow(() -> new IllegalArgumentException("Alojamento não encontrado"));
        alojamento.setEstadoLimpeza(EstadoLimpeza.PENDENTE);
        alojamentoRepository.save(alojamento);
    }

    /**
     * Marca um alojamento como concluído (limpeza feita).
     */
    public void marcarLimpezaConcluida(Long alojamentoId) {
        Alojamento alojamento = alojamentoRepository.findById(alojamentoId)
            .orElseThrow(() -> new IllegalArgumentException("Alojamento não encontrado"));
        alojamento.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
        alojamentoRepository.save(alojamento);
    }

    private DisponibilidadeAlojamentoDto toDisponibilidadeDto(Alojamento alojamento, LocalDate dataInicio, LocalDate dataFim) {
        DisponibilidadeAlojamentoDto dto = new DisponibilidadeAlojamentoDto(
            alojamento.getId(),
            alojamento.getIdentificacao(),
            alojamento.getTipo(),
            alojamento.getCapacidade()
        );
        dto.setDataInicio(dataInicio);
        dto.setDataFim(dataFim);
        dto.setDisponivel(true);
        return dto;
    }
}
