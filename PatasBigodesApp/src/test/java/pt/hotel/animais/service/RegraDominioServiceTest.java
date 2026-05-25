package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.enums.EstadoEstadia;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegraDominioServiceTest {

    private final RegraDominioService service = new RegraDominioService();

    // --- validarPeriodo ---

    @Test
    void validarPeriodoAceitaPeriodoValido() {
        service.validarPeriodo(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5));
    }

    @Test
    void validarPeriodoRejeitaDataInicioNula() {
        assertThatThrownBy(() -> service.validarPeriodo(null, LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatórias");
    }

    @Test
    void validarPeriodoRejeitaDataFimNula() {
        assertThatThrownBy(() -> service.validarPeriodo(LocalDate.now(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatórias");
    }

    @Test
    void validarPeriodoRejeitaInicioIgualFim() {
        LocalDate data = LocalDate.of(2026, 6, 1);
        assertThatThrownBy(() -> service.validarPeriodo(data, data))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("anterior");
    }

    @Test
    void validarPeriodoRejeitaInicioDepoisDeFim() {
        assertThatThrownBy(() -> service.validarPeriodo(LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("anterior");
    }

    // --- validarEstadiaAtiva ---

    @Test
    void validarEstadiaAtivaAceitaEstadiaEmCurso() {
        Estadia estadia = criarEstadia(EstadoEstadia.EM_CURSO);
        service.validarEstadiaAtiva(estadia);
    }

    @Test
    void validarEstadiaAtivaRejeitaEstadiaTerminada() {
        Estadia estadia = criarEstadia(EstadoEstadia.TERMINADA);
        assertThatThrownBy(() -> service.validarEstadiaAtiva(estadia))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("em curso");
    }

    @Test
    void validarEstadiaAtivaRejeitaNula() {
        assertThatThrownBy(() -> service.validarEstadiaAtiva(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não existe");
    }

    // --- normalizarPageable ---

    @Test
    void normalizarPageableRetornaDefaultQuandoNulo() {
        Pageable resultado = service.normalizarPageable(null);
        assertThat(resultado.getPageNumber()).isEqualTo(0);
        assertThat(resultado.getPageSize()).isEqualTo(10);
    }

    @Test
    void normalizarPageableRetornaOriginalQuandoValido() {
        Pageable pageable = PageRequest.of(2, 20);
        Pageable resultado = service.normalizarPageable(pageable);
        assertThat(resultado).isSameAs(pageable);
    }

    private Estadia criarEstadia(EstadoEstadia estado) {
        Estadia estadia = new Estadia();
        estadia.setId(1L);
        estadia.setEstado(estado);
        estadia.setDataInicio(LocalDateTime.now().minusDays(1));
        return estadia;
    }
}
