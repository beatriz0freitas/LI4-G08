package pt.hotel.animais.config;

import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura o repositório de eventos de auditoria usado pelo Spring Boot Actuator.
 *
 * O repositório em memória é suficiente para desenvolvimento e testes. Em ambiente
 * de produção, este bean pode ser substituído por uma implementação persistente ou por
 * integração com logs centralizados, mantendo os serviços a publicar
 * {@code AuditApplicationEvent}.
 */
@Configuration
public class AuditConfig {

    //NOTA:REVER ISTO, SE QUEREMOS OU NAO AUDITORIA, SE SIM, VER SE QUEREMOS GUARDAR EM MEMORIA OU NUMA BASE DE DADOS

    
    /**
     * Disponibiliza o repositório de eventos consultado pelo endpoint Actuator
     * {@code auditevents}.
     *
     * @return repositório em memória para eventos de auditoria
     */
    @Bean
    public AuditEventRepository auditEventRepository() {
        return new InMemoryAuditEventRepository();
    }
}
