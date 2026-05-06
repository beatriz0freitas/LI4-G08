package pt.hotel.animais.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.model.enums.TipoAlojamento;
import pt.hotel.animais.repository.AlojamentoRepository;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {"spring.jpa.hibernate.ddl-auto=update"})
@Transactional
public class AlojamentoServiceTimingTests {

    @Autowired
    private AlojamentoService alojamentoService;

    @Autowired
    private AlojamentoRepository alojamentoRepository;

    @BeforeEach
    public void seedAlojamentos() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Alojamento a = new Alojamento();
            a.setIdentificacao("B-" + i);
            a.setTipo(TipoAlojamento.CANINO);
            a.setCapacidade(1);
            a.setEstadoLimpeza(EstadoLimpeza.CONCLUIDO);
            alojamentoRepository.save(a);
        });
    }

    @Test
    public void disponibilidadeQueryShouldBeFast() {
        int runs = 20;
        long total = 0;
        for (int i = 0; i < runs; i++) {
            long s = System.nanoTime();
            alojamentoService.consultarDisponibilidade(
                java.time.LocalDate.now().plusDays(1),
                java.time.LocalDate.now().plusDays(2)
            );
            long e = System.nanoTime();
            total += (e - s);
        }

        long avgMs = (total / runs) / 1_000_000;
        // expect average < 300 ms in test environment
        assertTrue(avgMs < 300, "Consulta de disponibilidade demasiado lenta: " + avgMs + "ms");
    }
}
