package pt.hotel.animais.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.TipoAlojamentoTarifa;
import pt.hotel.animais.repository.TipoAlojamentoTarifaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gestão de tarifas por tipo de alojamento.
 * Acesso restrito ao diretor (ROLE_DIRETOR).
 */
@Service
@Transactional
public class TipoAlojamentoTarifaService {

    private final TipoAlojamentoTarifaRepository repository;

    @Autowired
    public TipoAlojamentoTarifaService(TipoAlojamentoTarifaRepository repository) {
        this.repository = repository;
    }

    /**
     * Obter a tarifa ativa para um tipo de alojamento.
     */
    @Transactional(readOnly = true)
    public Optional<TipoAlojamentoTarifa> obterTarifaAtiva(String tipo) {
        return repository.findActivoByTipo(normalizarTipo(tipo));
    }

    /**
     * Obter a tarifa diária para um tipo de alojamento.
     * Se não houver tarifa ativa para o tipo, lança exceção.
     */
    @Transactional(readOnly = true)
    public BigDecimal obterValorTarifa(String tipo) {
        String tipoNormalizado = normalizarTipo(tipo);
        return repository.findActivoByTipo(tipoNormalizado)
                .map(TipoAlojamentoTarifa::getTarifaDiaria)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Tarifa não configurada para tipo de alojamento: " + tipoNormalizado));
    }

    /**
     * Listar todas as tarifas ativas.
     */
    @Transactional(readOnly = true)
    public List<TipoAlojamentoTarifa> listarAtivas() {
        return repository.findAllAtivos();
    }

    /**
     * Listar todas as tarifas (ativas e inativas).
     */
    @Transactional(readOnly = true)
    public List<TipoAlojamentoTarifa> listarTodas() {
        return repository.findAllOrderByTipo();
    }

    /**
     * Criar uma nova tarifa para um tipo de alojamento.
     */
    public TipoAlojamentoTarifa criar(String tipo, BigDecimal tarifaDiaria) {
        String tipoNormalizado = normalizarTipo(tipo);
        if (tarifaDiaria == null || tarifaDiaria.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tarifa diária não pode ser nula ou negativa");
        }

        if (repository.findByTipoAlojamento(tipoNormalizado).isPresent()) {
            throw new IllegalArgumentException(
                "Já existe uma tarifa configurada para o tipo: " + tipoNormalizado);
        }

        TipoAlojamentoTarifa tarifa = new TipoAlojamentoTarifa(tipoNormalizado, tarifaDiaria);
        return repository.save(tarifa);
    }

    /**
     * Atualizar a tarifa diária de um tipo de alojamento.
     */
    public TipoAlojamentoTarifa atualizarTarifa(Long id, BigDecimal novaValor) {
        if (novaValor == null || novaValor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Nova tarifa não pode ser nula ou negativa");
        }

        TipoAlojamentoTarifa tarifa = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa não encontrada: " + id));

        tarifa.setTarifaDiaria(novaValor);
        return repository.save(tarifa);
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo de alojamento não pode ser vazio");
        }
        return tipo.trim().toUpperCase();
    }

    /**
     * Desativar um tipo de alojamento.
     */
    public void desativar(Long id) {
        TipoAlojamentoTarifa tarifa = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa não encontrada: " + id));

        tarifa.setAtivo(false);
        repository.save(tarifa);
    }

    /**
     * Reativar um tipo de alojamento.
     */
    public void reativar(Long id) {
        TipoAlojamentoTarifa tarifa = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa não encontrada: " + id));

        tarifa.setAtivo(true);
        repository.save(tarifa);
    }

    /**
     * Obter uma tarifa por ID.
     */
    @Transactional(readOnly = true)
    public Optional<TipoAlojamentoTarifa> obterPorId(Long id) {
        return repository.findById(id);
    }
}
