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

/**
 * Serviço de aplicação para gestão de colaboradores e respetivos perfis de acesso.
 *
 * Centraliza validações de unicidade, hashing de passwords e publicação de
 * eventos de auditoria. A remoção de colaboradores é lógica para preservar
 * rastreabilidade histórica.
 */
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

    /**
     * Lista todos os colaboradores, ativos e inativos.
     *
     * @return lista de colaboradores persistidos
     */
    @Override
    @Transactional(readOnly = true)
    public List<Colaborador> listarTodos() {
        return colaboradorRepository.findAll();
    }

    /**
     * Obtém um colaborador pelo identificador.
     *
     * @param id identificador do colaborador
     * @return colaborador encontrado
     * @throws IllegalArgumentException quando o colaborador não existe
     */
    @Override
    @Transactional(readOnly = true)
    public Colaborador obter(Long id) {
        return colaboradorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado"));
    }

    /**
     * Cria um colaborador com password armazenada como BCrypt.
     *
     * @param formDto dados validados do formulário
     * @return colaborador criado
     * @throws IllegalArgumentException quando a password falta ou username/email já existem
     */
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

    /**
     * Atualiza dados administrativos de um colaborador existente.
     *
     * <p>A password só é alterada quando o formulário traz um novo valor não vazio.</p>
     *
     * @param id identificador do colaborador
     * @param formDto dados validados do formulário
     * @return colaborador atualizado
     */
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

    /**
     * Desativa logicamente um colaborador.
     *
     * @param id identificador do colaborador
     */
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
