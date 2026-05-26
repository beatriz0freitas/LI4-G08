package pt.hotel.animais.service.auditoria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.enums.ResultadoAuditoria;
import pt.hotel.animais.repository.ColaboradorRepository;

import java.util.Map;
import java.util.Optional;

/**
 * Adaptador aplicacional para registar auditoria das operações de negócio.
 *
 * Resolve o colaborador autenticado quando a operação não recebe explicitamente
 * o identificador do autor e delega a persistência para {@link IAuditoriaService}.
 */
@Service
public class AuditoriaOperacaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaOperacaoService.class);

    private final IAuditoriaService auditoriaService;
    private final ColaboradorRepository colaboradorRepository;

    public AuditoriaOperacaoService(IAuditoriaService auditoriaService,
                                    ColaboradorRepository colaboradorRepository) {
        this.auditoriaService = auditoriaService;
        this.colaboradorRepository = colaboradorRepository;
    }

    /**
     * Regista uma operação concluída com sucesso usando o utilizador autenticado.
     *
     * @param operacao operação funcional executada
     * @param entidade entidade afetada
     * @param entityId identificador da entidade afetada
     * @param acao ação CRUD aproximada
     * @param detalhes contexto adicional em JSON
     */
    public void registarSucesso(String operacao,
                                String entidade,
                                Long entityId,
                                String acao,
                                Map<String, Object> detalhes) {
        registarSucesso(null, operacao, entidade, entityId, acao, detalhes);
    }

    /**
     * Regista uma operação concluída com sucesso, preferindo o autor indicado.
     *
     * @param utilizadorId identificador do colaborador autor, opcional
     * @param operacao operação funcional executada
     * @param entidade entidade afetada
     * @param entityId identificador da entidade afetada
     * @param acao ação CRUD aproximada
     * @param detalhes contexto adicional em JSON
     */
    public void registarSucesso(Long utilizadorId,
                                String operacao,
                                String entidade,
                                Long entityId,
                                String acao,
                                Map<String, Object> detalhes) {
        Long autorId = utilizadorId != null ? utilizadorId : obterUtilizadorAtualId().orElse(null);
        if (autorId == null) {
            LOGGER.warn("Evento de auditoria {} ignorado: utilizador autenticado não encontrado", operacao);
            return;
        }

        auditoriaService.registarEvento(
            autorId,
            operacao,
            entidade,
            entityId,
            acao,
            detalhes,
            ResultadoAuditoria.SUCESSO,
            null
        );
    }

    /**
     * Regista uma falha funcional usando o utilizador autenticado quando existe.
     *
     * @param operacao operação funcional tentada
     * @param entidade entidade afetada
     * @param entityId identificador da entidade afetada
     * @param acao ação CRUD aproximada
     * @param detalhes contexto adicional em JSON
     * @param motivoFalha motivo da falha
     */
    public void registarFalha(String operacao,
                              String entidade,
                              Long entityId,
                              String acao,
                              Map<String, Object> detalhes,
                              String motivoFalha) {
        obterUtilizadorAtualId().ifPresentOrElse(
            autorId -> auditoriaService.registarEvento(
                autorId,
                operacao,
                entidade,
                entityId,
                acao,
                detalhes,
                ResultadoAuditoria.FALHA,
                motivoFalha
            ),
            () -> LOGGER.warn("Falha de auditoria {} ignorada: utilizador autenticado não encontrado", operacao)
        );
    }

    /**
     * Resolve o identificador do colaborador autenticado pelo SecurityContext.
     *
     * @return identificador do colaborador quando existe sessão autenticada
     */
    public Optional<Long> obterUtilizadorAtualId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String principal = authentication.getName();
        if (principal == null || principal.isBlank() || "anonymousUser".equals(principal)) {
            return Optional.empty();
        }

        Optional<Long> idNumerico = procurarPorIdNumerico(principal);
        if (idNumerico.isPresent()) {
            return idNumerico;
        }

        return colaboradorRepository.findByUsername(principal)
            .map(Colaborador::getId);
    }

    private Optional<Long> procurarPorIdNumerico(String principal) {
        try {
            Long id = Long.valueOf(principal);
            return colaboradorRepository.findById(id).map(Colaborador::getId);
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
