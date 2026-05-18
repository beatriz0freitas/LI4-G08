package pt.hotel.animais.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.repository.EstadiaRepository;

@SpringBootTest
@Transactional
public class PagamentoCheckInCalculoTest {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private EstadiaRepository estadiaRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(pagamentoService);
        Assertions.assertNotNull(estadiaRepository);
    }
}
