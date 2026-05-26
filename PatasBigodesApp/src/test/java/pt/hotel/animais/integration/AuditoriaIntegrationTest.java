package pt.hotel.animais.integration;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pt.hotel.animais.dto.ColaboradorFormDto;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.model.enums.TipoColaborador;
import pt.hotel.animais.repository.ColaboradorRepository;
import pt.hotel.animais.service.ColaboradorService;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditoriaIntegrationTest {

    private final ColaboradorRepository colaboradorRepository = mock(ColaboradorRepository.class);
    private final AuditoriaOperacaoService auditoriaOperacaoService = mock(AuditoriaOperacaoService.class);
    private final ColaboradorService colaboradorService = new ColaboradorService(
        colaboradorRepository,
        new BCryptPasswordEncoder(),
        auditoriaOperacaoService
    );

    @Test
    void criarEditarEDesativarColaboradorDevemEmitirEventosDeAuditoria() {
        when(colaboradorRepository.existsByUsername("novo")).thenReturn(false);
        when(colaboradorRepository.existsByEmail("novo@hotel.local")).thenReturn(false);
        when(colaboradorRepository.existsByUsernameAndIdNot("novo", 10L)).thenReturn(false);
        when(colaboradorRepository.existsByEmailAndIdNot("novo@hotel.local", 10L)).thenReturn(false);
        when(colaboradorRepository.save(any(Colaborador.class))).thenAnswer(invocation -> {
            Colaborador colaborador = invocation.getArgument(0);
            if (colaborador.getId() == null) {
                colaborador.setId(10L);
            }
            return colaborador;
        });

        Colaborador criado = colaboradorService.criar(form());
        when(colaboradorRepository.findById(10L)).thenReturn(Optional.of(criado));

        colaboradorService.atualizar(10L, form());
        colaboradorService.desativar(10L);

        verify(auditoriaOperacaoService).registarSucesso(eq("CRIAR_COLABORADOR"), eq("Colaborador"), eq(10L), eq("CREATE"), any());
        verify(auditoriaOperacaoService).registarSucesso(eq("EDITAR_COLABORADOR"), eq("Colaborador"), eq(10L), eq("UPDATE"), any());
        verify(auditoriaOperacaoService).registarSucesso(eq("DESATIVAR_COLABORADOR"), eq("Colaborador"), eq(10L), eq("UPDATE"), any());
    }

    private ColaboradorFormDto form() {
        ColaboradorFormDto form = new ColaboradorFormDto();
        form.setUsername("novo");
        form.setNome("Novo Colaborador");
        form.setEmail("novo@hotel.local");
        form.setPassword("segredo123");
        form.setTipoColaborador(TipoColaborador.CUIDADOR);
        form.setAtivo(true);
        return form;
    }
}
