package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.DisponibilidadeAlojamentoDto;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.enums.Especie;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.ReservaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlojamentoService implements IAlojamentoService {

    private final AlojamentoRepository alojamentoRepository;
    private final IAvailabilityDomainService availabilityDomainService;
    private final ReservaRepository reservaRepository;
    private final EstadiaRepository estadiaRepository;

    public List<Alojamento> listarTodos() {
        return alojamentoRepository.findAllByOrderByIdentificacaoAsc();
    }

    /**
     * Conta os alojamentos disponíveis para receção.
     */
    public long contarAlojamentosDisponiveis() {
        return alojamentoRepository.countDisponiveisOperacionais();
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
     * Um alojamento é considerado disponível se estiver limpo, sem reserva
     * sobreposta e sem estadia ativa.
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
     * Constrói o mapa operacional único: cada alojamento surge uma vez com o
     * seu estado atual e o bloqueio relevante para o período consultado.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadeAlojamentoDto> consultarMapaDisponibilidade(
        LocalDate dataInicio, LocalDate dataFim, String tipo
    ) {
        validarPeriodo(dataInicio, dataFim);

        return alojamentoRepository.findAllByOrderByIdentificacaoAsc().stream()
            .filter(alojamento -> tipo == null || tipo.isBlank() || "TODOS".equalsIgnoreCase(tipo)
                || tipo.equals(alojamento.getTipo()))
            .map(alojamento -> toMapaDto(alojamento, dataInicio, dataFim))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarTipos() {
        return alojamentoRepository.findAllByOrderByIdentificacaoAsc().stream()
            .map(Alojamento::getTipo)
            .filter(tipo -> tipo != null && !tipo.isBlank())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Verifica se um alojamento específico está disponível para um período.
     */
    public boolean estaDisponivel(Long alojamentoId, LocalDate dataInicio, LocalDate dataFim) {
        return availabilityDomainService.estaDisponivel(alojamentoId, dataInicio, dataFim);
    }

    /**
     * Verifica disponibilidade e compatibilidade com a espécie do animal.
     */
    public boolean estaDisponivel(Long alojamentoId, LocalDate dataInicio, LocalDate dataFim, Especie especie) {
        return availabilityDomainService.estaDisponivel(alojamentoId, dataInicio, dataFim, especie);
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
    @Transactional
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

    private DisponibilidadeAlojamentoDto toMapaDto(
        Alojamento alojamento, LocalDate dataInicio, LocalDate dataFim
    ) {
        DisponibilidadeAlojamentoDto dto = toDisponibilidadeDto(alojamento, dataInicio, dataFim);

        Optional<Estadia> estadiaAtiva = estadiaRepository.findEmCursoPorAlojamentoComDetalhes(alojamento.getId());
        if (estadiaAtiva.isPresent()) {
            Estadia estadia = estadiaAtiva.get();
            dto.setEstado("OCUPADO");
            dto.setDisponivel(false);
            dto.setEstadiaId(estadia.getId());
            dto.setMotivoIndisponibilidade("Estadia em curso");
            if (estadia.getReserva() != null && estadia.getReserva().getAnimal() != null) {
                dto.setAnimalNome(estadia.getReserva().getAnimal().getNome());
            }
            return dto;
        }

        if (alojamento.getEstadoLimpeza() != EstadoLimpeza.CONCLUIDO) {
            dto.setEstado("LIMPEZA");
            dto.setDisponivel(false);
            dto.setMotivoIndisponibilidade("Limpeza pendente");
            return dto;
        }

        List<Reserva> reservas = reservaRepository.findActiveReservasInPeriodWithDetalhes(
            alojamento.getId(), dataInicio, dataFim
        );
        if (!reservas.isEmpty()) {
            Reserva reserva = reservas.get(0);
            dto.setEstado("RESERVADO");
            dto.setDisponivel(false);
            dto.setReservaId(reserva.getId());
            dto.setDataReservaInicio(reserva.getDataInicio());
            dto.setDataReservaFim(reserva.getDataFim());
            dto.setMotivoIndisponibilidade("Reserva no período consultado");
            if (reserva.getAnimal() != null) {
                dto.setAnimalNome(reserva.getAnimal().getNome());
            }
            return dto;
        }

        dto.setEstado("LIVRE");
        return dto;
    }

    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null || !dataInicio.isBefore(dataFim)) {
            throw new IllegalArgumentException("Data de início deve ser anterior a data de fim");
        }
    }
}
