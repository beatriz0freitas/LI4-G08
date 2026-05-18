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
}
