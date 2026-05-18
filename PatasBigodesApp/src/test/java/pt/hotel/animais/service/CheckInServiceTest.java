package pt.hotel.animais.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.repository.EstadiaRepository;

@SpringBootTest
@Transactional
public class CheckInServiceTest {

    @Autowired
    private EstadiaService estadiaService;

    @Autowired
    private EstadiaRepository estadiaRepository;

    @Test
    void estadiaService_is_autowired() {
        Assertions.assertNotNull(estadiaService);
    }

    @Test
    void estadiaRepository_is_autowired() {
        Assertions.assertNotNull(estadiaRepository);
    }

    @Test
    void abrirEstadiaPorReserva_fails_for_invalid_reserva() {
        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, 
            () -> estadiaService.abrirEstadiaPorReserva(9999L));
    }
}
