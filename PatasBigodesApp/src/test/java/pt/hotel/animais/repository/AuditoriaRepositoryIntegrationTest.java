package pt.hotel.animais.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.data.domain.PageRequest;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.auditoria.AuditoriaEvento;
import pt.hotel.animais.model.enums.ResultadoAuditoria;
import pt.hotel.animais.model.enums.TipoColaborador;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AuditoriaRepositoryIntegrationTest {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Test
    void deveGuardarEFiltrarEventosDeAuditoria() {
        Colaborador colaborador = new Colaborador();
        colaborador.setUsername("diretor");
        colaborador.setNome("Diretor Geral");
        colaborador.setEmail("diretor@hotel.local");
        colaborador.setPasswordHash("hash");
        colaborador.setTipoColaborador(TipoColaborador.DIRETOR);
        colaborador.setAtivo(true);
        colaborador = colaboradorRepository.save(colaborador);

        AuditoriaEvento evento1 = new AuditoriaEvento();
        evento1.setUtilizador(colaborador);
        evento1.setOperacao("CRIAR_COLABORADOR");
        evento1.setEntidade("Colaborador");
        evento1.setEntityId(10L);
        evento1.setAcao("CREATE");
        evento1.setDetalhes(Map.of("campo", "nome"));
        evento1.setResultado(ResultadoAuditoria.SUCESSO);
        evento1.setTimestamp(LocalDateTime.now().minusDays(1));
        auditoriaRepository.save(evento1);

        AuditoriaEvento evento2 = new AuditoriaEvento();
        evento2.setUtilizador(colaborador);
        evento2.setOperacao("EDITAR_COLABORADOR");
        evento2.setEntidade("Colaborador");
        evento2.setEntityId(11L);
        evento2.setAcao("UPDATE");
        evento2.setDetalhes(Map.of("campo", "email"));
        evento2.setResultado(ResultadoAuditoria.SUCESSO);
        evento2.setTimestamp(LocalDateTime.now());
        auditoriaRepository.save(evento2);

        assertThat(auditoriaRepository.findByTimestampBetween(
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().plusDays(1),
            PageRequest.of(0, 10)
        ).getTotalElements()).isEqualTo(2);

        assertThat(auditoriaRepository.findByUtilizador_IdAndTimestampBetween(
            colaborador.getId(),
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().plusDays(1),
            PageRequest.of(0, 10)
        ).getTotalElements()).isEqualTo(2);

        assertThat(auditoriaRepository.findByOperacaoContainingIgnoreCaseAndTimestampBetween(
            "criar",
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().plusDays(1),
            PageRequest.of(0, 10)
        ).getTotalElements()).isEqualTo(1);

        assertThat(auditoriaRepository.findByEntidadeContainingIgnoreCaseAndTimestampBetween(
            "Colaborador",
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().plusDays(1),
            PageRequest.of(0, 10)
        ).getTotalElements()).isEqualTo(2);

        List<AuditoriaEvento> antigos = auditoriaRepository.findByTimestampBefore(LocalDateTime.now().minusHours(12));
        assertThat(antigos).hasSize(1);

        long removidos = auditoriaRepository.deleteByTimestampBefore(LocalDateTime.now().minusHours(12));
        assertThat(removidos).isEqualTo(1L);
    }
}
