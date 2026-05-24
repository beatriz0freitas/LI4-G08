package pt.hotel.animais.service;

import pt.hotel.animais.dto.ColaboradorFormDto;
import pt.hotel.animais.model.Colaborador;

import java.util.List;

/**
 * Contrato de aplicação para administração de colaboradores.
 *
 * As operações deste serviço são reservadas ao perfil de direção e suportam a
 * gestão de perfis de acesso definida pela matriz RBAC.
 */
public interface IColaboradorService {
    /**
     * Lista colaboradores registados.
     *
     * @return colaboradores existentes
     */
    List<Colaborador> listarTodos();

    /**
     * Obtém um colaborador por identificador.
     *
     * @param id identificador do colaborador
     * @return colaborador encontrado
     */
    Colaborador obter(Long id);

    /**
     * Cria um colaborador a partir dos dados do formulário.
     *
     * @param formDto dados de criação
     * @return colaborador criado
     */
    Colaborador criar(ColaboradorFormDto formDto);

    /**
     * Atualiza dados de um colaborador existente.
     *
     * @param id identificador do colaborador
     * @param formDto dados de atualização
     * @return colaborador atualizado
     */
    Colaborador atualizar(Long id, ColaboradorFormDto formDto);

    /**
     * Desativa logicamente um colaborador.
     *
     * @param id identificador do colaborador
     */
    void desativar(Long id);
}
