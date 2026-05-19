package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.RegistoCuidado;
import java.util.List;

@Repository
public interface RegistoCuidadoRepository extends JpaRepository<RegistoCuidado, Long> {
    List<RegistoCuidado> findByEstadiaIdOrderByDataHoraDesc(Long estadiaId);
}
