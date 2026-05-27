package pt.hotel.animais.service.auditoria;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.auditoria.AuditoriaFiltroDTO;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.auditoria.AuditoriaEvento;
import pt.hotel.animais.model.enums.ResultadoAuditoria;
import pt.hotel.animais.repository.AuditoriaRepository;
import pt.hotel.animais.repository.ColaboradorRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Serviço central de auditoria para operações críticas.
 */
@Service
@Transactional
public class AuditoriaService implements IAuditoriaService {

    private static final int LIMITE_DETALHES_JSON = 2000;

    private final AuditoriaRepository auditoriaRepository;
    private final ColaboradorRepository colaboradorRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository,
                            ColaboradorRepository colaboradorRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    @Override
    public AuditoriaEvento registarEvento(Long utilizadorId,
                                          String operacao,
                                          String entidade,
                                          Long entityId,
                                          String acao,
                                          Map<String, Object> detalhes,
                                          ResultadoAuditoria resultado,
                                          String motivoFalha) {
        validarCamposObrigatorios(utilizadorId, operacao, entidade, entityId, acao, resultado);

        Colaborador utilizador = colaboradorRepository.findById(utilizadorId)
            .orElseThrow(() -> new IllegalArgumentException("Utilizador de auditoria não encontrado"));

        Map<String, Object> detalhesNormalizados = Optional.ofNullable(detalhes)
            .map(LinkedHashMap::new)
            .orElseGet(LinkedHashMap::new);
        validarTamanhoDetalhes(detalhesNormalizados);

        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setUtilizador(utilizador);
        evento.setOperacao(operacao.trim());
        evento.setEntidade(entidade.trim());
        evento.setEntityId(entityId);
        evento.setAcao(acao.trim());
        evento.setDetalhes(detalhesNormalizados);
        evento.setResultado(resultado);
        evento.setMotivoFalha(motivoFalha != null && !motivoFalha.isBlank() ? motivoFalha.trim() : null);

        return auditoriaRepository.save(evento);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditoriaEvento> consultarPorPeriodo(LocalDate dataInicio,
                                                     LocalDate dataFim,
                                                     AuditoriaFiltroDTO filtros,
                                                     Pageable pageable) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
        Specification<AuditoriaEvento> specification = construirSpecification(inicio, fim, filtros);
        return auditoriaRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditoriaEvento> consultarPorUtilizador(Long utilizadorId,
                                                        AuditoriaFiltroDTO filtros,
                                                        Pageable pageable) {
        AuditoriaFiltroDTO criterios = filtros != null ? filtros : new AuditoriaFiltroDTO();
        criterios.setUtilizadorId(utilizadorId);
        LocalDate dataInicio = criterios.getDataInicio() != null ? criterios.getDataInicio() : LocalDate.now().minusYears(1);
        LocalDate dataFim = criterios.getDataFim() != null ? criterios.getDataFim() : LocalDate.now();
        return consultarPorPeriodo(dataInicio, dataFim, criterios, pageable);
    }

    @Override
    public long limparzardosAntigos(int anosRetencao) {
        if (anosRetencao <= 0) {
            throw new IllegalArgumentException("anosRetencao deve ser superior a zero");
        }
        LocalDateTime limite = LocalDateTime.now().minusYears(anosRetencao);
        return auditoriaRepository.deleteByTimestampBefore(limite);
    }

    private void validarCamposObrigatorios(Long utilizadorId,
                                            String operacao,
                                            String entidade,
                                            Long entityId,
                                            String acao,
                                            ResultadoAuditoria resultado) {
        if (utilizadorId == null) {
            throw new IllegalArgumentException("Utilizador é obrigatório para auditoria");
        }
        if (operacao == null || operacao.isBlank()) {
            throw new IllegalArgumentException("Operação é obrigatória para auditoria");
        }
        if (entidade == null || entidade.isBlank()) {
            throw new IllegalArgumentException("Entidade é obrigatória para auditoria");
        }
        if (entityId == null && !permiteEntidadeNaoPersistida(operacao, entidade)) {
            throw new IllegalArgumentException("EntityId é obrigatório para auditoria");
        }
        if (acao == null || acao.isBlank()) {
            throw new IllegalArgumentException("Ação é obrigatória para auditoria");
        }
        if (resultado == null) {
            throw new IllegalArgumentException("Resultado é obrigatório para auditoria");
        }
    }

    private boolean permiteEntidadeNaoPersistida(String operacao, String entidade) {
        return "RELATORIO_GERADO".equals(operacao.trim())
            && "Relatorio".equals(entidade.trim());
    }

    private void validarTamanhoDetalhes(Map<String, Object> detalhes) {
        if (detalhes == null || detalhes.isEmpty()) {
            return;
        }
        String json = detalhes.toString();
        if (json.length() > LIMITE_DETALHES_JSON) {
            throw new IllegalArgumentException("Detalhes de auditoria excedem 2000 caracteres");
        }
    }

    private Specification<AuditoriaEvento> construirSpecification(LocalDateTime inicio,
                                                                  LocalDateTime fim,
                                                                  AuditoriaFiltroDTO filtros) {
        Specification<AuditoriaEvento> specification = (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("timestamp"), inicio, fim);

        if (filtros == null) {
            return specification;
        }

        if (filtros.getUtilizadorId() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("utilizador").get("id"), filtros.getUtilizadorId()));
        }
        if (filtros.getOperacao() != null && !filtros.getOperacao().isBlank()) {
            String operacao = "%" + filtros.getOperacao().toLowerCase() + "%";
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("operacao")), operacao));
        }
        if (filtros.getEntidade() != null && !filtros.getEntidade().isBlank()) {
            String entidade = "%" + filtros.getEntidade().toLowerCase() + "%";
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("entidade")), entidade));
        }
        if (filtros.getResultado() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("resultado"), filtros.getResultado()));
        }
        return specification;
    }
}
