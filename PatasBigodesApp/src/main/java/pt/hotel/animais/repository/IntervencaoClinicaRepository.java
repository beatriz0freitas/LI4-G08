package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.IntervencaoClinica;
import java.util.List;

@Repository
public interface IntervencaoClinicaRepository extends JpaRepository<IntervencaoClinica, Long> {
    List<IntervencaoClinica> findByEstadiaId(Long estadiaId);

    List<IntervencaoClinica> findByEstadiaReservaAnimalIdOrderByDataHoraDesc(Long animalId);
}
