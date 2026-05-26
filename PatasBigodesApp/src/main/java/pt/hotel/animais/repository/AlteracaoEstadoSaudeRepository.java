package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.AlteracaoEstadoSaude;
import java.util.List;

@Repository
public interface AlteracaoEstadoSaudeRepository extends JpaRepository<AlteracaoEstadoSaude, Long> {
    List<AlteracaoEstadoSaude> findByEstadiaIdOrderByDataHoraDesc(Long estadiaId);

    List<AlteracaoEstadoSaude> findByEstadiaReservaAnimalIdOrderByDataHoraDesc(Long animalId);
}
