package pt.hotel.animais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.EstadoLimpeza;
import pt.hotel.animais.repository.AlojamentoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlojamentoServiceTest {

    @Mock
    private AlojamentoRepository alojamentoRepository;

    @InjectMocks
    private AlojamentoService alojamentoService;

    @Test
    void contarAlojamentosDisponiveisDeveContarConcluidos() {
        when(alojamentoRepository.countByEstadoLimpeza(EstadoLimpeza.CONCLUIDO)).thenReturn(2L);

        long resultado = alojamentoService.contarAlojamentosDisponiveis();

        assertThat(resultado).isEqualTo(2L);
    }

    @Test
    void listarTodosDeveDelegarParaOrdenacaoPorIdentificacao() {
        when(alojamentoRepository.findAllByOrderByIdentificacaoAsc()).thenReturn(java.util.List.of(
                    new Alojamento(1L, "Box 1", null, null, EstadoLimpeza.PENDENTE, null)
        ));

        var resultado = alojamentoService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdentificacao()).isEqualTo("Box 1");
    }
}