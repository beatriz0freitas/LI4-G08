package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.TipoServicoExtra;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para gestão do catálogo de tipos de serviços extra.
 */
@Repository
public interface TipoServicoExtraRepository extends JpaRepository<TipoServicoExtra, Long> {

    /**
     * Busca um tipo de serviço por nome.
     */
    Optional<TipoServicoExtra> findByNome(String nome);

    /**
     * Lista apenas tipos de serviço ativos.
     */
    @Query("SELECT tse FROM TipoServicoExtra tse WHERE tse.ativo = true ORDER BY tse.nome ASC")
    List<TipoServicoExtra> findAllAtivos();

    /**
     * Lista todos os tipos de serviço (ativos e inativos).
     */
    @Query("SELECT tse FROM TipoServicoExtra tse ORDER BY tse.nome ASC")
    List<TipoServicoExtra> findAllOrderByNome();
}
