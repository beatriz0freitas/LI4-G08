package pt.hotel.animais.config;

import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    //NOTA:REVER ISTO, SE QUEREMOS OU NAO AUDITORIA, SE SIM, VER SE QUEREMOS GUARDAR EM MEMORIA OU NUMA BASE DE DADOS

    @Bean
    public AuditEventRepository auditEventRepository() {
        return new InMemoryAuditEventRepository();
    }
}
