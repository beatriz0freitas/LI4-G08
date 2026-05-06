package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.Tutor;
import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Tutor.
 */
@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {
    
    /**
     * Procura um tutor pelo NIF (único).
     */
    Optional<Tutor> findByNif(String nif);
    
    /**
     * Procura tutores pelo nome (busca parcial).
     */
    List<Tutor> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Procura tutores pelo email.
     */
    Optional<Tutor> findByEmail(String email);
    
    /**
     * Procura tutores pelo contacto.
     */
    List<Tutor> findByContactoContaining(String contacto);
}
