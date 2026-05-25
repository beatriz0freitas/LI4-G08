package pt.hotel.animais.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RNF-01 — 10 utilizadores em simultâneo devem obter resposta em menos de 5 segundos.
 * Requer DB de testes activa: make db-reset-test antes de correr.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ConcurrentAccessTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void dezUtilizadoresEmSimultaneoDevemReceberPaginaDeLogin() throws InterruptedException {
        int n = 10;
        ExecutorService pool = Executors.newFixedThreadPool(n);
        CountDownLatch ready = new CountDownLatch(n);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(n);
        List<Integer> statuses = Collections.synchronizedList(new ArrayList<>());
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < n; i++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    ResponseEntity<String> resp = restTemplate.getForEntity("/login", String.class);
                    statuses.add(resp.getStatusCode().value());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    errors.add(e);
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        long t0 = System.currentTimeMillis();
        start.countDown();
        boolean finished = done.await(10, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - t0;
        pool.shutdown();

        assertThat(errors).as("Exceções durante carga simultânea: %s", errors).isEmpty();
        assertThat(finished).as("Nem todos os %d pedidos terminaram em 10s", n).isTrue();
        assertThat(statuses).hasSize(n);
        assertThat(statuses).allSatisfy(s -> assertThat(s).isBetween(200, 399));
        assertThat(elapsed).as("10 pedidos em paralelo demoraram %dms (limite: 5000ms)", elapsed)
                           .isLessThan(5000L);
    }
}
