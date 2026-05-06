package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.enums.Especie;
import java.util.List;

/**
 * Repository para a entidade Animal.
 */
@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    
    /**
     * Procura animais de um tutor específico.
     */
    List<Animal> findByTutorId(Long tutorId);
    
    /**
     * Procura animais pelo nome (busca parcial).
     */
    List<Animal> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Procura animais de um tutor pelo nome.
     */
    List<Animal> findByTutorIdAndNomeContainingIgnoreCase(Long tutorId, String nome);
    
    /**
     * Procura animais pela espécie.
     */
    List<Animal> findByEspecie(Especie especie);
    
    /**
     * Conta quantos animais um tutor tem.
     */
    long countByTutorId(Long tutorId);
}
