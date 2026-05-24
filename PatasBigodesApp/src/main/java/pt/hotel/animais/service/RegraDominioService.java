package pt.hotel.animais.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class RegraDominioService {

    /**
     * Valida invariantes simples do domínio. Exemplo: não permitir datas inválidas.
     */
    public void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Período inválido: datas obrigatórias");
        }
        if (!inicio.isBefore(fim)) {
            throw new IllegalArgumentException("Data de início deve ser anterior à data de fim");
        }
    }

    // Placeholder para futuras validações transversais (overbooking, autorizações, etc.)

    /**
     * Valida que a estadia está em estado EM_CURSO.
     */
    public void validarEstadiaAtiva(pt.hotel.animais.model.Estadia estadia) {
        if (estadia == null) throw new IllegalArgumentException("Estadia não existe");
        if (estadia.getEstado() != pt.hotel.animais.model.enums.EstadoEstadia.EM_CURSO) {
            throw new IllegalArgumentException("Estadia não está em curso");
        }
    }

    /**
     * Normaliza um Pageable com defaults simples quando for nulo.
     */
    public org.springframework.data.domain.Pageable normalizarPageable(org.springframework.data.domain.Pageable pageable) {
        if (pageable == null) return org.springframework.data.domain.PageRequest.of(0, 10);
        return pageable;
    }
}

