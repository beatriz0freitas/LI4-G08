package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.hotel.animais.model.ServicoExtra;
import java.util.List;

@Repository
public interface ServicoExtraRepository extends JpaRepository<ServicoExtra, Long> {
    List<ServicoExtra> findByEstadiaId(Long estadiaId);
}
