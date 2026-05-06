package pt.hotel.animais.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.repository.TutorRepository;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {"spring.jpa.hibernate.ddl-auto=update"})
@Transactional
public class TutorServiceTimingTests {

    @Autowired
    private TutorService tutorService;

    @Autowired
    private TutorRepository tutorRepository;

    @BeforeEach
    public void seedTutors() {
        // seed a moderate number of tutors for search performance
        IntStream.rangeClosed(1, 200).forEach(i -> {
            Tutor t = new Tutor();
            t.setNome("Tutor " + i + " Nome");
            t.setNif(String.format("PT%09d", 100000000 + i));
            t.setContacto("9000000" + i);
            t.setEmail("tutor" + i + "@test.local");
            tutorRepository.save(t);
        });
    }

    @Test
    public void searchByNameShouldBeFast() {
        // measure several runs and take average
        int runs = 10;
        long totalNanos = 0;
        for (int i = 0; i < runs; i++) {
            long start = System.nanoTime();
            tutorService.procurarPorNome("Tutor 1");
            long end = System.nanoTime();
            totalNanos += (end - start);
        }

        long avgMillis = (totalNanos / runs) / 1_000_000;
        // threshold: average search < 200 ms in test environment
        assertTrue(avgMillis < 200, "Pesquisa por nome demasiado lenta: " + avgMillis + "ms");
    }
}
