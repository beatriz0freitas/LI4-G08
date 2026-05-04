package pt.hotel.animais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.enums.EstadoLimpeza;

import java.util.List;

public interface AlojamentoRepository extends JpaRepository<Alojamento, Long> {
    List<Alojamento> findByEstadoLimpeza(EstadoLimpeza estadoLimpeza);

    List<Alojamento> findAllByOrderByIdentificacaoAsc();

    long countByEstadoLimpeza(EstadoLimpeza estadoLimpeza);
}
