package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.enums.Especie;
import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Animal.
 */
@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    
    /**
     * Procura animais de um tutor específico.
     */
    @EntityGraph(attributePaths = "tutor")
    List<Animal> findByTutorId(Long tutorId);
    
    /**
     * Procura animais pelo nome (procura parcial).
     */
    @EntityGraph(attributePaths = "tutor")
    List<Animal> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Procura animais de um tutor pelo nome.
     */
    @EntityGraph(attributePaths = "tutor")
    List<Animal> findByTutorIdAndNomeContainingIgnoreCase(Long tutorId, String nome);
    
    /**
     * Procura animais pela espécie.
     */
    @EntityGraph(attributePaths = "tutor")
    List<Animal> findByEspecie(Especie especie);
    
    /**
     * Conta quantos animais um tutor tem.
     */
    long countByTutorId(Long tutorId);

    /**
     * Obtém um animal com o tutor já carregado.
     */
    @Override
    @EntityGraph(attributePaths = "tutor")
    Optional<Animal> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Animal a WHERE a.id = :id")
    Optional<Animal> findByIdForUpdate(@Param("id") Long id);

    /**
     * Lista todos os animais com o tutor já carregado.
     */
    @Override
    @EntityGraph(attributePaths = "tutor")
    List<Animal> findAll();
}
