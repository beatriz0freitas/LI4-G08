package pt.hotel.animais.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.TipoServicoExtra;
import pt.hotel.animais.repository.TipoServicoExtraRepository;

import java.util.List;
import java.util.Optional;

/**
 * Serviço para gestão do catálogo de tipos de serviços extra.
 * Acesso restrito ao diretor (ROLE_DIRETOR).
 */
@Service
@Transactional
public class TipoServicoExtraService {

    private final TipoServicoExtraRepository repository;

    @Autowired
    public TipoServicoExtraService(TipoServicoExtraRepository repository) {
        this.repository = repository;
    }

    /**
     * Listar todos os tipos de serviço ativos.
     */
    @Transactional(readOnly = true)
    public List<TipoServicoExtra> listarAtivos() {
        return repository.findAllAtivos();
    }

    /**
     * Listar todos os tipos de serviço (ativos e inativos).
     */
    @Transactional(readOnly = true)
    public List<TipoServicoExtra> listarTodos() {
        return repository.findAllOrderByNome();
    }

    /**
     * Criar um novo tipo de serviço extra.
     */
    public TipoServicoExtra criar(String nome, String descricao) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do tipo de serviço não pode ser vazio");
        }

        if (repository.findByNome(nome).isPresent()) {
            throw new IllegalArgumentException(
                "Já existe um tipo de serviço com o nome: " + nome);
        }

        TipoServicoExtra tipo = new TipoServicoExtra(nome, descricao);
        return repository.save(tipo);
    }

    /**
     * Atualizar um tipo de serviço extra.
     */
    public TipoServicoExtra atualizar(Long id, String nome, String descricao) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do tipo de serviço não pode ser vazio");
        }

        TipoServicoExtra tipo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de serviço não encontrado: " + id));

        // Verificar se o novo nome já existe (e não é o mesmo tipo)
        Optional<TipoServicoExtra> existente = repository.findByNome(nome);
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                "Já existe um tipo de serviço com o nome: " + nome);
        }

        tipo.setNome(nome);
        tipo.setDescricao(descricao);
        return repository.save(tipo);
    }

    /**
     * Desativar um tipo de serviço extra.
     */
    public void desativar(Long id) {
        TipoServicoExtra tipo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de serviço não encontrado: " + id));

        tipo.setAtivo(false);
        repository.save(tipo);
    }

    /**
     * Reativar um tipo de serviço extra.
     */
    public void reativar(Long id) {
        TipoServicoExtra tipo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de serviço não encontrado: " + id));

        tipo.setAtivo(true);
        repository.save(tipo);
    }

    /**
     * Obter um tipo de serviço por ID.
     */
    @Transactional(readOnly = true)
    public Optional<TipoServicoExtra> obterPorId(Long id) {
        return repository.findById(id);
    }

    /**
     * Obter um tipo de serviço por nome.
     */
    @Transactional(readOnly = true)
    public Optional<TipoServicoExtra> obterPorNome(String nome) {
        return repository.findByNome(nome);
    }

    /**
     * Validar se um tipo de serviço está ativo.
     */
    @Transactional(readOnly = true)
    public boolean estaAtivo(Long tipoId) {
        return repository.findById(tipoId)
                .map(TipoServicoExtra::getAtivo)
                .orElse(false);
    }
}
