package pt.hotel.animais.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.repository.EstadiaRepository;

@SpringBootTest
@Transactional
public class CheckOutSequenceServiceTest {

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
    void checkOut_fails_for_invalid_estadia() {
        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> estadiaService.checkOut(9999L));
    }
}
