package pt.hotel.animais.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.ColaboradorFormDto;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.repository.ColaboradorRepository;
import pt.hotel.animais.service.auditoria.AuditoriaOperacaoService;

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
    private final AuditoriaOperacaoService auditoriaOperacaoService;

    public ColaboradorService(ColaboradorRepository colaboradorRepository,
                              PasswordEncoder passwordEncoder,
                              AuditoriaOperacaoService auditoriaOperacaoService) {
        this.colaboradorRepository = colaboradorRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaOperacaoService = auditoriaOperacaoService;
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
        auditoriaOperacaoService.registarSucesso(
            "CRIAR_COLABORADOR",
            "Colaborador",
            criado.getId(),
            "CREATE",
            detalhesColaborador(criado)
        );
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
        Map<String, Object> detalhesAlteracao = detalhesAlteracao(colaborador, formDto);
        aplicarCampos(colaborador, formDto);
        if (formDto.getPassword() != null && !formDto.getPassword().isBlank()) {
            colaborador.setPasswordHash(passwordEncoder.encode(formDto.getPassword()));
        }
        Colaborador atualizado = colaboradorRepository.save(colaborador);
        auditoriaOperacaoService.registarSucesso(
            "EDITAR_COLABORADOR",
            "Colaborador",
            atualizado.getId(),
            "UPDATE",
            detalhesAlteracao
        );
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
        Map<String, Object> detalhes = new LinkedHashMap<>();
        detalhes.put("ativoAnterior", colaborador.isAtivo());
        detalhes.put("ativoNovo", false);
        colaborador.setAtivo(false);
        colaboradorRepository.save(colaborador);
        auditoriaOperacaoService.registarSucesso(
            "DESATIVAR_COLABORADOR",
            "Colaborador",
            colaborador.getId(),
            "UPDATE",
            detalhes
        );
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

    private Map<String, Object> detalhesColaborador(Colaborador colaborador) {
        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("colaboradorId", colaborador.getId());
        dados.put("username", colaborador.getUsername());
        dados.put("email", colaborador.getEmail());
        dados.put("tipoColaborador", colaborador.getTipoColaborador() != null ? colaborador.getTipoColaborador().name() : null);
        dados.put("ativo", colaborador.isAtivo());
        return dados;
    }

    private Map<String, Object> detalhesAlteracao(Colaborador colaborador, ColaboradorFormDto formDto) {
        Map<String, Object> alteracoes = new LinkedHashMap<>();
        registarAlteracao(alteracoes, "username", colaborador.getUsername(), formDto.getUsername());
        registarAlteracao(alteracoes, "nome", colaborador.getNome(), formDto.getNome());
        registarAlteracao(alteracoes, "email", colaborador.getEmail(), formDto.getEmail());
        registarAlteracao(
            alteracoes,
            "tipoColaborador",
            colaborador.getTipoColaborador() != null ? colaborador.getTipoColaborador().name() : null,
            formDto.getTipoColaborador() != null ? formDto.getTipoColaborador().name() : null
        );
        registarAlteracao(alteracoes, "ativo", colaborador.isAtivo(), formDto.isAtivo());
        if (formDto.getPassword() != null && !formDto.getPassword().isBlank()) {
            alteracoes.put("password", "alterada");
        }
        return Map.of("camposAlterados", alteracoes);
    }

    private void registarAlteracao(Map<String, Object> alteracoes, String campo, Object anterior, Object novo) {
        Object valorNovo = novo instanceof String texto ? texto.trim() : novo;
        if (!java.util.Objects.equals(anterior, valorNovo)) {
            Map<String, Object> delta = new LinkedHashMap<>();
            delta.put("anterior", anterior);
            delta.put("novo", valorNovo);
            alteracoes.put(campo, delta);
        }
    }
}
