package pt.hotel.animais.service.auditoria;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.hotel.animais.dto.auditoria.AuditoriaFiltroDTO;
import pt.hotel.animais.model.auditoria.AuditoriaEvento;
import pt.hotel.animais.model.enums.ResultadoAuditoria;

import java.time.LocalDate;
import java.util.Map;

/**
 * Contrato público para gestão de eventos de auditoria.
 */
public interface IAuditoriaService {

    AuditoriaEvento registarEvento(Long utilizadorId,
                                   String operacao,
                                   String entidade,
                                   Long entityId,
                                   String acao,
                                   Map<String, Object> detalhes,
                                   ResultadoAuditoria resultado,
                                   String motivoFalha);

    Page<AuditoriaEvento> consultarPorPeriodo(LocalDate dataInicio,
                                              LocalDate dataFim,
                                              AuditoriaFiltroDTO filtros,
                                              Pageable pageable);

    Page<AuditoriaEvento> consultarPorUtilizador(Long utilizadorId,
                                                  AuditoriaFiltroDTO filtros,
                                                  Pageable pageable);

    long limparzardosAntigos(int anosRetencao);
}
