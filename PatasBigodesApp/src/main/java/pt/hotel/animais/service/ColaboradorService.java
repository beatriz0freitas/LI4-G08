package pt.hotel.animais.service;

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.ColaboradorFormDto;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.repository.ColaboradorRepository;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional
public class ColaboradorService implements IColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public ColaboradorService(ColaboradorRepository colaboradorRepository,
                              PasswordEncoder passwordEncoder,
                              ApplicationEventPublisher eventPublisher) {
        this.colaboradorRepository = colaboradorRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Colaborador> listarTodos() {
        return colaboradorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Colaborador obter(Long id) {
        return colaboradorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado"));
    }

    @Override
    public Colaborador criar(ColaboradorFormDto formDto) {
        if (formDto.getPassword() == null || formDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password é obrigatória na criação do colaborador");
        }
        validarUnicidade(formDto, null);

        Colaborador colaborador = new Colaborador();
        aplicarCampos(colaborador, formDto);
        colaborador.setPasswordHash(passwordEncoder.encode(formDto.getPassword()));
        Colaborador criado = colaboradorRepository.save(colaborador);
        publicarAuditoria("COLABORADOR_CRIADO", criado);
        return criado;
    }

    @Override
    public Colaborador atualizar(Long id, ColaboradorFormDto formDto) {
        Colaborador colaborador = obter(id);
        validarUnicidade(formDto, id);
        aplicarCampos(colaborador, formDto);
        if (formDto.getPassword() != null && !formDto.getPassword().isBlank()) {
            colaborador.setPasswordHash(passwordEncoder.encode(formDto.getPassword()));
        }
        Colaborador atualizado = colaboradorRepository.save(colaborador);
        publicarAuditoria("COLABORADOR_ATUALIZADO", atualizado);
        return atualizado;
    }

    @Override
    public void desativar(Long id) {
        Colaborador colaborador = obter(id);
        colaborador.setAtivo(false);
        colaboradorRepository.save(colaborador);
        publicarAuditoria("COLABORADOR_DESATIVADO", colaborador);
    }

    private void aplicarCampos(Colaborador colaborador, ColaboradorFormDto formDto) {
        colaborador.setUsername(formDto.getUsername().trim());
        colaborador.setNome(formDto.getNome().trim());
        colaborador.setEmail(formDto.getEmail().trim());
        colaborador.setTipoColaborador(formDto.getTipoColaborador());
        colaborador.setAtivo(formDto.isAtivo());
    }

    private void validarUnicidade(ColaboradorFormDto formDto, Long idAtual) {
        boolean usernameExiste = idAtual == null
            ? colaboradorRepository.existsByUsername(formDto.getUsername())
            : colaboradorRepository.existsByUsernameAndIdNot(formDto.getUsername(), idAtual);
        if (usernameExiste) {
            throw new IllegalArgumentException("Já existe um colaborador com esse username");
        }

        boolean emailExiste = idAtual == null
            ? colaboradorRepository.existsByEmail(formDto.getEmail())
            : colaboradorRepository.existsByEmailAndIdNot(formDto.getEmail(), idAtual);
        if (emailExiste) {
            throw new IllegalArgumentException("Já existe um colaborador com esse email");
        }
    }

    private void publicarAuditoria(String tipoEvento, Colaborador colaborador) {
        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("colaboradorId", colaborador.getId());
        dados.put("username", colaborador.getUsername());
        dados.put("tipoColaborador", colaborador.getTipoColaborador() != null ? colaborador.getTipoColaborador().name() : null);

        eventPublisher.publishEvent(new AuditApplicationEvent(
            utilizadorAtual(),
            tipoEvento,
            dados
        ));
    }

    private String utilizadorAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "sistema";
    }
}
